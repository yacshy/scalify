//! axum 路由：控制面**唯一**对外通道 `/api/v1/*`（绑 10.42.0.1:9090）。
//!
//! 契约：docs/openapi.yaml（字段/枚举）、docs/API.md §1 端点清单 / §4 错误码 /
//! §5 请求响应形状。Rust 不监听 8889/8189/8554/9997，不接触帧 / RTP / H.264 数据。
//!
//! 两套命名域，绝不混用（R5 消除二义）：
//! - 请求体 `StreamFeedback` 全 **snake_case**（feedback.rs / openapi.yaml）；
//! - `GET /api/v1/stream/status` 响应全 **camelCase**（bitrateKbps / firstFrameMs …）；
//! - `GET /api/v1/config` 响应全 **camelCase**（ConfigView）；
//! - `FeedbackAck` 用 `target_kbps`，请求体回显字段是 `current_target_kbps`，两者不同。
//!
//! P0 凭据隔离：WHEP `Location: /scalp/whep/<secret>` 路径段是 DELETE 能力凭据，
//! 与 `201` 的 `ID` 头（= trace_id）是两次独立生成的 UUID。本模块只接收端侧上报的
//! `trace_id`（来自 ID 头），**绝不接触、绝不记录 Location secret**。

use axum::body::Bytes;
use axum::extract::State;
use axum::http::StatusCode;
use axum::response::{IntoResponse, Response};
use axum::routing::{get, post};
use axum::{Json, Router};
use serde_json::{json, Value};

use crate::feedback::{FeedbackAck, StreamFeedback};
use crate::mediamtx::{self, ProbeOutcome};
use crate::pipeline;
use crate::state::{DeviceState, SharedState};

/// 装配 7 个控制面端点。`with_state` 之后状态类型被擦除为 `Router<()>`，
/// 可直接交给 `axum::serve`（main.rs）。
pub fn router(state: SharedState) -> Router {
    Router::new()
        .route("/api/v1/health", get(health))
        .route("/api/v1/time", get(time))
        .route("/api/v1/stream/status", get(stream_status))
        .route("/api/v1/config", get(config_view))
        .route("/api/v1/stream/start", post(stream_start))
        .route("/api/v1/stream/stop", post(stream_stop))
        .route("/api/v1/stream/feedback", post(stream_feedback))
        .with_state(state)
}

/// 统一成功包装 `{code:0, data, message:""}`（API.md §5）。
fn ok(data: Value) -> Response {
    (
        StatusCode::OK,
        Json(json!({ "code": 0, "data": data, "message": "" })),
    )
        .into_response()
}

/// 统一错误包装。HTTP status 与业务码分层：WHEP 是 HTTP status，控制面用 code
/// （API.md §4「先分清两层」）。不把异常直接抛给上游（runtime-defensive）。
fn biz_err(status: StatusCode, code: i32, message: impl Into<String>) -> Response {
    (
        status,
        Json(json!({ "code": code, "data": {}, "message": message.into() })),
    )
        .into_response()
}

/// GET /api/v1/health — 控制面探活（Health: control/mediamtx/pipeline/ap ∈ ok|down）。
/// ap 由网络层（hostapd/dnsmasq，deploy/）保证，Rust 不探测，回显已配置态，
/// 真实 AP 状态由 deploy/preflight.sh 实机核验；避免控制面越界。
async fn health(State(state): State<SharedState>) -> Response {
    let mediamtx = match mediamtx::probe_paths(&state).await {
        // api_enabled=false（Win11 fake / 未部署 mediamtx）：不产生媒体面假告警。
        ProbeOutcome::Ready | ProbeOutcome::Skipped => "ok",
        ProbeOutcome::NoPublisher | ProbeOutcome::Error => "down",
    };
    let pipeline = {
        let rt = state.runtime.read().await;
        if matches!(rt.state, DeviceState::Starting | DeviceState::Streaming) {
            "ok"
        } else {
            "down"
        }
    };
    ok(json!({
        "control": "ok",
        "mediamtx": mediamtx,
        "pipeline": pipeline,
        "ap": "ok",
    }))
}

/// GET /api/v1/time — 热点内无公网 NTP，端侧启动时做一次时钟偏移校准，
/// 供 first_frame_ms 与全链路 P95 对齐（API.md §5 / openapi.yaml TimeView）。
async fn time() -> Response {
    ok(json!({ "server_unix_ms": chrono::Utc::now().timestamp_millis() }))
}

/// GET /api/v1/stream/status — 重试前必查。**camelCase**（StreamStatus）。
/// clients 由 WHEP reader 决定，Rust 控制面不监听 WHEP：首版恒回显 runtime 值，
/// 精确计数属媒体面职责，不在此伪造。channel 为 AP 信道实测回显值（0=未实测）。
async fn stream_status(State(state): State<SharedState>) -> Response {
    let c = &state.config;
    // path 从 rtsp 环回 URL 派生，避免与配置脱节（不写死字面量 "scalp"）。
    let path = c.rtsp.url.rsplit('/').next().unwrap_or("scalp");
    let rt = state.runtime.read().await;
    ok(json!({
        "device": if rt.device_ok { "ok" } else { "missing" },
        "state": rt.state.as_str(),
        "encoder": c.encoder.name,
        "path": path,
        "clients": rt.clients,
        "codec": "H264",
        "profileLevelId": "42e01f",
        "captureFormat": c.capture.format,
        "width": c.capture.width,
        "height": c.capture.height,
        "fps": c.capture.fps,
        "bitrateKbps": c.encoder.bitrate_kbps,
        "gop": c.gop_frames(),
        "channel": c.network.ap_channel,
        "phyMbps": 0,
        "firstFrameMs": rt.first_frame_ms,
        "uptimeSec": rt.uptime_sec(),
    }))
}

/// GET /api/v1/config — 脱敏回显（ConfigView，camelCase）。
/// **绝不回显** security.wpa2_passphrase 或任何密钥（Secret 新类型 + 白名单）。
async fn config_view(State(state): State<SharedState>) -> Response {
    let c = &state.config;
    let capture_source = if c.is_fake() {
        "videotestsrc"
    } else if c.capture.format.eq_ignore_ascii_case("YUYV") {
        "v4l2src-yuyv"
    } else {
        "v4l2src-mjpg"
    };
    ok(json!({
        "captureSource": capture_source,
        "captureFormat": c.capture.format,
        "captureWidth": c.capture.width,
        "captureHeight": c.capture.height,
        "captureFps": c.capture.fps,
        "encoderProfile": c.encoder.name,
        "apSubnet": c.network.ap_subnet,
        "apChannel": c.network.ap_channel,
        "defaultGatewayAdvertised": c.network.default_gateway_advertised,
        "internetForwarded": c.network.internet_forwarded,
        "mediamtxVersion": c.network.mediamtx_version,
    }))
}

/// POST /api/v1/stream/start — 启动采集编码管道（**零自动重试**，非幂等写，
/// 客户端须先 GET status；API.md §7）。409 = 仅控制面资源冲突，与 WHEP 无关。
async fn stream_start(State(state): State<SharedState>) -> Response {
    {
        let rt = state.runtime.read().await;
        if matches!(rt.state, DeviceState::Starting | DeviceState::Streaming) {
            return biz_err(StatusCode::CONFLICT, 4003, "pipeline already running");
        }
    }
    // pipeline::start 是纯同步短临界区（std Mutex，不跨 await），直接调用。
    // fake 模式只记录命令串、不 spawn（Win11 绝不执行 Linux 专有能力）。
    match pipeline::start(&state) {
        Ok(()) => {
            let mut rt = state.runtime.write().await;
            rt.state = DeviceState::Starting;
            rt.consecutive_failures = 0;
            drop(rt);
            state.logger.write(&json!({
                "event": "stream_start",
                "stage": state.config.profile,
                "gop_frames": state.config.gop_frames(),
                "bitrate_kbps": state.config.encoder.bitrate_kbps,
                "ts": chrono::Utc::now().timestamp_millis(),
            }));
            ok(json!({}))
        }
        Err(e) => biz_err(StatusCode::CONFLICT, 4003, e),
    }
}

/// POST /api/v1/stream/stop — 停止管道（零自动重试）。
/// 404 = code 4005「无运行中的会话」，视为已结束、幂等返回。
async fn stream_stop(State(state): State<SharedState>) -> Response {
    {
        let rt = state.runtime.read().await;
        if rt.state == DeviceState::Idle {
            return biz_err(StatusCode::NOT_FOUND, 4005, "no active session");
        }
    }
    pipeline::stop(&state);
    let mut rt = state.runtime.write().await;
    rt.state = DeviceState::Idle;
    rt.clients = 0;
    drop(rt);
    ok(json!({}))
}

/// POST /api/v1/stream/feedback — 端侧 getStats 1Hz 上报（唯一统计端点）。
///
/// 决策 2=A：首版**冻结 2500 kbps**，只上报 + JSONL 归因，响应恒 `decision="frozen"`，
/// **绝不自动调码率、绝不改编码器参数**。用 `Bytes` 手工解析以完全掌控错误格式，
/// 保证任何输入都返回统一 `{code,data,message}`（不用 JsonRejection 的默认纯文本）。
///
/// P0：日志只记录 `trace_id`（= 201 ID 头），不接触 WHEP Location secret。
async fn stream_feedback(State(state): State<SharedState>, body: Bytes) -> Response {
    let fb: StreamFeedback = match serde_json::from_slice(body.as_ref()) {
        Ok(v) => v,
        Err(e) => {
            return biz_err(
                StatusCode::BAD_REQUEST,
                4001,
                format!("invalid JSON body: {e}"),
            )
        }
    };
    if let Err(e) = fb.validate() {
        return biz_err(StatusCode::BAD_REQUEST, 4001, e);
    }

    let ack = FeedbackAck::frozen();

    // 与 server 侧事件同写一份 JSONL、同名同字段，供四档 P50/P95 归因
    // （API.md §8）。窗口增量口径由端侧保证，这里不把累计值「转换」成增量。
    state.logger.write(&json!({
        "event": "feedback",
        "trace_id": fb.trace_id,
        "sample_id": fb.sample_id,
        "session_seq": fb.session_seq,
        "window_ms": fb.window_ms,
        "ts": fb.ts,
        "stage": fb.stage,
        "packets_lost": fb.packets_lost,
        "nack_count": fb.nack_count,
        "pli_count": fb.pli_count,
        "retransmitted_packets_received": fb.retransmitted_packets_received,
        "frames_decoded": fb.frames_decoded,
        "loss_ratio": fb.loss_ratio,
        "jitter_buf_ms": fb.jitter_buf_ms,
        "decode_ms": fb.decode_ms,
        "decode_ms_p95": fb.decode_ms_p95,
        "e2e_ms_p50": fb.e2e_ms_p50,
        "e2e_ms_p95": fb.e2e_ms_p95,
        "freeze_count": fb.freeze_count,
        "current_target_kbps": fb.current_target_kbps,
        "app_version": fb.app_version,
        "accepted": ack.accepted,
        "decision": ack.decision,
        "server_ts": chrono::Utc::now().timestamp_millis(),
    }));

    ok(json!({
        "accepted": ack.accepted,
        "target_kbps": ack.target_kbps,
        "decision": ack.decision,
    }))
}
