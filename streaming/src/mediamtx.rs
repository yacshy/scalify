//! mediamtx Control API 探流。只走 127.0.0.1:9997，路径固定
//! `/v3/paths/get/scalp` 与 `/v3/webrtc/sessions/list`
//! （**严禁**写成 `/v3/webrtcsessions/list`，docs/API.md §1）。
//! 所有调用带超时（Client 构造时统一设置），超时返回 Error 不 panic。

use serde_json::Value;

use crate::state::SharedState;

#[derive(Debug, Clone, Copy, PartialEq, Eq)]
pub enum ProbeOutcome {
    Ready,
    NoPublisher,
    /// api_enabled=false（Win11 fake / 未部署 mediamtx）：不告警、不做状态迁移。
    Skipped,
    Error,
}

pub async fn probe_paths(state: &SharedState) -> ProbeOutcome {
    if !state.config.mediamtx.api_enabled {
        return ProbeOutcome::Skipped;
    }
    let base = state.config.mediamtx.control_api_base.trim_end_matches('/');
    let url = format!("{base}/v3/paths/get/scalp");
    match state.http.get(&url).send().await {
        Ok(resp) if resp.status().is_success() => match resp.json::<Value>().await {
            // ready / sourceReady 字段名标「需实测」（docs/API.md §1），
            // 这里优先读 ready，缺失时退化按「拿到 200 即认为路径存在」。
            Ok(v) => {
                let ready = v.get("ready").and_then(Value::as_bool).unwrap_or(true);
                if ready { ProbeOutcome::Ready } else { ProbeOutcome::NoPublisher }
            }
            Err(e) => {
                tracing::warn!(error = %e, "control api json parse failed");
                ProbeOutcome::Error
            }
        },
        Ok(resp) => {
            tracing::warn!(status = %resp.status(), "control api non-2xx");
            ProbeOutcome::Error
        }
        Err(e) => {
            tracing::warn!(error = %e, "control api probe failed (timeout or unreachable)");
            ProbeOutcome::Error
        }
    }
}

/// 排查残留 session 用。返回原始 JSON，调用方自行脱敏
/// （session secret 绝不写进日志或 feedback）。
/// 首版主链路不调用（Phase 2 排查 reader 占满时启用），故显式允许死代码。
#[allow(dead_code)]
pub async fn list_sessions(state: &SharedState) -> Option<Value> {
    if !state.config.mediamtx.api_enabled {
        return None;
    }
    let base = state.config.mediamtx.control_api_base.trim_end_matches('/');
    let url = format!("{base}/v3/webrtc/sessions/list");
    state.http.get(&url).send().await.ok()?.json::<Value>().await.ok()
}
