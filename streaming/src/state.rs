//! 设备状态机与进程内共享状态。
//! 状态枚举来自 docs/API.md §5 / openapi.yaml StreamStatus.state。

use std::sync::{Arc, Mutex};

use tokio::sync::RwLock;

use crate::config::Config;
use crate::logging::JsonlLogger;

#[derive(Debug, Clone, Copy, PartialEq, Eq, serde::Serialize)]
#[serde(rename_all = "lowercase")]
pub enum DeviceState {
    Idle,
    Starting,
    Streaming,
    #[allow(dead_code)] // 首版无降级逻辑；watchdog 恢复分支已引用，Phase 2 启用
    Degraded,
    Failed,
    Reconnecting,
}

impl DeviceState {
    pub fn as_str(self) -> &'static str {
        match self {
            DeviceState::Idle => "idle",
            DeviceState::Starting => "starting",
            DeviceState::Streaming => "streaming",
            DeviceState::Degraded => "degraded",
            DeviceState::Failed => "failed",
            DeviceState::Reconnecting => "reconnecting",
        }
    }
}

pub struct DeviceRuntime {
    pub state: DeviceState,
    pub device_ok: bool,
    pub clients: u32,
    pub first_frame_ms: u64,
    pub started_at_ms: i64,
    pub consecutive_failures: u32,
}

impl DeviceRuntime {
    pub fn new() -> Self {
        Self {
            state: DeviceState::Idle,
            device_ok: false,
            clients: 0,
            first_frame_ms: 0,
            started_at_ms: chrono::Utc::now().timestamp_millis(),
            consecutive_failures: 0,
        }
    }

    pub fn uptime_sec(&self) -> u64 {
        let delta = chrono::Utc::now().timestamp_millis() - self.started_at_ms;
        if delta > 0 { delta as u64 / 1000 } else { 0 }
    }
}

pub struct AppState {
    pub config: Config,
    pub runtime: RwLock<DeviceRuntime>,
    pub logger: Arc<JsonlLogger>,
    pub http: reqwest::Client,
    /// GStreamer 子进程句柄。仅 spawn_processes=true 时非空；
    /// 用 std Mutex 而非 tokio Mutex，因为 start/stop 是纯同步短临界区，不跨 await。
    pub pipeline_child: Mutex<Option<std::process::Child>>,
}

pub type SharedState = Arc<AppState>;

impl AppState {
    pub fn new(config: Config, logger: Arc<JsonlLogger>, http: reqwest::Client) -> Self {
        Self {
            config,
            runtime: RwLock::new(DeviceRuntime::new()),
            logger,
            http,
            pipeline_child: Mutex::new(None),
        }
    }
}
