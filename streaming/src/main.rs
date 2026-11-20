//! scalp-control — Rust 控制面入口。
//!
//! 职责边界（用户 Q3 / docs/streaming-plan.md R3）：Rust 只做控制面。
//! 不监听 8889/8189/8554/9997，不接触任何帧 / RTP / H.264 数据，不实现 WebRTC。
//! 媒体链路由 GStreamer + mediamtx 承担；Rust 只做设备状态机、配置、会话、
//! 结构化日志、看门狗、统计反馈，以及通过子进程「编排」GStreamer 的启停。

mod api;
mod config;
mod feedback;
mod logging;
mod mediamtx;
mod pipeline;
mod state;
mod watchdog;

use std::sync::Arc;

use crate::config::Config;
use crate::logging::JsonlLogger;
use crate::state::{AppState, SharedState};

#[tokio::main]
async fn main() -> Result<(), Box<dyn std::error::Error>> {
    tracing_subscriber::fmt()
        .with_env_filter(
            tracing_subscriber::EnvFilter::try_from_default_env()
                .unwrap_or_else(|_| tracing_subscriber::EnvFilter::new("info")),
        )
        .with_target(false)
        .init();

    let mut cfg = Config::load()?;
    cfg.finalize();

    tracing::info!(
        profile = %cfg.profile,
        bind = %cfg.server.bind_addr,
        gop_frames = cfg.gop_frames(),
        bitrate_kbps = cfg.encoder.bitrate_kbps,
        "scalp-control starting (control plane only)"
    );

    let logger = Arc::new(JsonlLogger::new(&cfg.logging.jsonl_path));
    let http = reqwest::Client::builder()
        .timeout(std::time::Duration::from_millis(cfg.mediamtx.probe_timeout_ms))
        .build()?;

    let state: SharedState = Arc::new(AppState::new(cfg, logger, http));

    // 看门狗：周期探流 + 掉线状态迁移。首版只上报，不自动重启管道。
    watchdog::spawn(state.clone());

    let app = api::router(state.clone());
    let listener = tokio::net::TcpListener::bind(&state.config.server.bind_addr).await?;
    tracing::info!(addr = %state.config.server.bind_addr, "control plane listening");

    axum::serve(listener, app)
        .with_graceful_shutdown(async {
            if tokio::signal::ctrl_c().await.is_ok() {
                tracing::info!("SIGINT received, shutting down");
            }
        })
        .await?;

    // 退出时停掉采集子进程（进程编排，仍属控制面）。
    pipeline::stop(&state);
    Ok(())
}
