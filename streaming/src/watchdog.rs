//! 看门狗：周期探 mediamtx Control API + 掉线状态迁移 + 5001/5002 结构化日志。
//!
//! 契约：docs/API.md §4 错误码 / §7「进程层 systemd Restart=always」/ §8 可观测。
//! 首版 `watchdog.auto_restart = false`：只上报、不自动重启管道；
//! 进程自愈交给 systemd（deploy/ 其他成员负责），不与端侧重连互相计数。
//!
//! 状态机（DeviceState，state.rs）：
//!   Idle ──start──> Starting ──probe Ready──> Streaming
//!   Streaming ──NoPublisher/Error(<threshold)──> Reconnecting
//!   Reconnecting ──probe Ready──> Streaming
//!   Reconnecting ──连续失败 >=failure_threshold──> Failed
//!   Skipped（api_enabled=false，Win11 fake / 未部署 mediamtx）→ 不做任何迁移、不告警
//!
//! P0 凭据隔离：本模块只探 `/v3/paths/get/scalp`，不读 WHEP session 资源、不接触
//! Location / session secret；日志里的 trace_id 只作占位字段，绝不写入凭据。

use serde_json::json;

use crate::mediamtx::{self, ProbeOutcome};
use crate::pipeline;
use crate::state::{DeviceState, SharedState};

/// 在 main.rs 里 tokio::spawn 一个后台任务。返回 ()，句柄不保留（随进程生命周期）。
pub fn spawn(state: SharedState) {
    let interval_ms = state.config.mediamtx.probe_interval_ms.max(1000);
    tracing::info!(interval_ms, "watchdog started (control plane only)");

    tokio::spawn(async move {
        let mut ticker = tokio::time::interval(std::time::Duration::from_millis(interval_ms));
        // 第一次 tick 立即返回；跳过它，避免启动瞬间就误判一次掉线。
        ticker.tick().await;
        loop {
            ticker.tick().await;
            let outcome = mediamtx::probe_paths(&state).await;
            handle_outcome(&state, outcome).await;
        }
    });
}

async fn handle_outcome(state: &SharedState, outcome: ProbeOutcome) {
    match outcome {
        // Win11 fake / 未部署 mediamtx：不告警、不迁移，避免假故障污染归因基线。
        ProbeOutcome::Skipped => {}

        ProbeOutcome::Ready => on_ready(state).await,

        ProbeOutcome::NoPublisher | ProbeOutcome::Error => on_failure(state, outcome).await,
    }
}

async fn on_ready(state: &SharedState) {
    let mut rt = state.runtime.write().await;
    let prev = rt.state;
    rt.device_ok = true;
    rt.consecutive_failures = 0;
    // Idle 保持不动：用户未启动会话时，mediamtx 有 publisher 也不代表本会话在预览。
    // Failed 允许被一次成功探流恢复（显式上报），不必强制走 start。
    if matches!(
        prev,
        DeviceState::Starting | DeviceState::Reconnecting | DeviceState::Degraded | DeviceState::Failed
    ) {
        rt.state = DeviceState::Streaming;
    }
    let recovered = rt.state == DeviceState::Streaming && prev != DeviceState::Streaming;
    drop(rt);

    if recovered {
        state.logger.write(&json!({
            "event": "watchdog_recovered",
            "state": "streaming",
            "stage": state.config.profile,
            "ts": chrono::Utc::now().timestamp_millis(),
        }));
        tracing::info!("watchdog: stream recovered -> streaming");
    }
}

async fn on_failure(state: &SharedState, outcome: ProbeOutcome) {
    let threshold = state.config.watchdog.failure_threshold.max(1);
    let reason = match outcome {
        ProbeOutcome::NoPublisher => "no_publisher",
        ProbeOutcome::Error => "probe_error_or_timeout",
        _ => "unknown",
    };

    let (failures, state_now) = {
        let mut rt = state.runtime.write().await;
        rt.device_ok = false;
        rt.consecutive_failures = rt.consecutive_failures.saturating_add(1);
        let prev = rt.state;
        // Idle 状态不参与掉线迁移（没有会话就无所谓掉线）。
        if !matches!(prev, DeviceState::Idle) {
            if rt.consecutive_failures >= threshold {
                rt.state = DeviceState::Failed;
            } else if matches!(prev, DeviceState::Streaming | DeviceState::Starting) {
                rt.state = DeviceState::Reconnecting;
            }
        }
        (rt.consecutive_failures, rt.state)
    };

    // 5001 设备掉线：每次失败都结构化上报，带连续次数与阈值，供 P50/P95 归因。
    state.logger.write(&json!({
        "event": "watchdog_device_offline",
        "code": 5001,
        "reason": reason,
        "consecutive_failures": failures,
        "failure_threshold": threshold,
        "state": state_now.as_str(),
        "stage": state.config.profile,
        "ts": chrono::Utc::now().timestamp_millis(),
    }));
    tracing::warn!(
        reason,
        failures,
        threshold,
        state = state_now.as_str(),
        "watchdog: publisher probe failed"
    );

    // 5002 看门狗重启管道：仅 auto_restart=true 时执行（首版 false，执行器留空，
    // 进程自愈交 systemd）。执行点是对 GStreamer 子进程的编排（控制面），不碰码流。
    if state.config.watchdog.auto_restart && failures >= threshold {
        state.logger.write(&json!({
            "event": "watchdog_pipeline_restart",
            "code": 5002,
            "consecutive_failures": failures,
            "stage": state.config.profile,
            "ts": chrono::Utc::now().timestamp_millis(),
        }));
        pipeline::stop(state);
        match pipeline::start(state) {
            Ok(()) => {
                let mut rt = state.runtime.write().await;
                rt.state = DeviceState::Starting;
                rt.consecutive_failures = 0;
            }
            Err(e) => tracing::error!(error = %e, "watchdog: pipeline restart failed"),
        }
    }
}
