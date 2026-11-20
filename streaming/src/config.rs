//! 参数化配置 + SCALP_PROFILE + key-int-max 派生。
//!
//! 权威口径：docs/streaming-plan.md R3 §4、docs/API.md §3/§5。
//! 硬约束：key-int-max 必须由 capture.fps 派生，禁止写死 30
//! （退档 15fps 时写死会让 GOP 静默变 2 秒）。

use serde::Deserialize;
use std::path::PathBuf;

/// WPA2 口令 / 密钥的新类型：Debug 只打印 ***，绝不进日志或响应。
/// GET /api/v1/config 直接不序列化 [Config::security]，双重保险。
/// 字段按设计「只存不读」：控制面不需要口令，任何读取都会扩大泄漏面。
#[derive(Clone, Deserialize, Default)]
pub struct Secret(#[allow(dead_code)] String); // 刻意不读：P0 凭据隔离，见 README §7

impl std::fmt::Debug for Secret {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        write!(f, "\"***\"")
    }
}

#[derive(Debug, Clone, Deserialize)]
pub struct Config {
    pub profile: String,
    pub server: ServerConfig,
    pub capture: CaptureConfig,
    pub encoder: EncoderConfig,
    pub rtsp: RtspConfig,
    pub mediamtx: MediamtxConfig,
    pub network: NetworkConfig,
    /// 密钥字段。Config 从不整体序列化；GET /api/v1/config 只回显白名单字段。
    /// 刻意不读：仅用于解析配置文件时兜底，绝不回显（P0 凭据隔离）。
    #[allow(dead_code)]
    pub security: SecurityConfig,
    pub logging: LoggingConfig,
    pub pipeline: PipelineConfig,
    pub watchdog: WatchdogConfig,
}

#[derive(Debug, Clone, Deserialize)]
pub struct ServerConfig {
    pub bind_addr: String,
}

#[derive(Debug, Clone, Deserialize)]
pub struct CaptureConfig {
    pub device: String,
    pub format: String,
    pub width: u32,
    pub height: u32,
    pub fps: u32,
}

#[derive(Debug, Clone, Deserialize)]
pub struct EncoderConfig {
    pub name: String,
    /// 决策 2=A：首版冻结 2500 kbps。feedback 路径绝不修改此值。
    pub bitrate_kbps: u32,
}

#[derive(Debug, Clone, Deserialize)]
pub struct RtspConfig {
    pub url: String,
    pub protocols: String,
}

#[derive(Debug, Clone, Deserialize)]
pub struct MediamtxConfig {
    pub control_api_base: String,
    pub api_enabled: bool,
    pub probe_timeout_ms: u64,
    pub probe_interval_ms: u64,
}

#[derive(Debug, Clone, Deserialize)]
pub struct NetworkConfig {
    pub ap_subnet: String,
    pub ap_channel: u32,
    pub default_gateway_advertised: bool,
    pub internet_forwarded: bool,
    pub mediamtx_version: String,
}

#[derive(Debug, Clone, Deserialize)]
pub struct SecurityConfig {
    #[allow(dead_code)] // 刻意不读：口令不进任何响应/日志
    pub wpa2_passphrase: Secret,
}

#[derive(Debug, Clone, Deserialize)]
pub struct LoggingConfig {
    pub jsonl_path: String,
}

#[derive(Debug, Clone, Deserialize)]
pub struct PipelineConfig {
    /// Win11 / 非 Linux 上强制 false，绝不真的执行 v4l2 / GStreamer / hostapd。
    pub spawn_processes: bool,
}

#[derive(Debug, Clone, Deserialize)]
pub struct WatchdogConfig {
    /// 首版 false：只上报 5001/5002，不自动重启管道（进程自愈交给 systemd）。
    pub auto_restart: bool,
    pub failure_threshold: u32,
}

impl Config {
    /// 加载顺序：SCALP_CONFIG 显式路径 > SCALP_PROFILE 选 config 文件 > default.toml。
    /// 用 CARGO_MANIFEST_DIR 定位，避免依赖当前工作目录。
    pub fn load() -> Result<Self, Box<dyn std::error::Error>> {
        let manifest = PathBuf::from(env!("CARGO_MANIFEST_DIR"));
        let path = match std::env::var("SCALP_CONFIG") {
            Ok(p) => PathBuf::from(p),
            Err(_) => {
                let profile = std::env::var("SCALP_PROFILE").unwrap_or_default();
                let name = if profile == "win11-fake" {
                    "win11-fake.toml"
                } else {
                    "default.toml"
                };
                manifest.join("config").join(name)
            }
        };
        let text = std::fs::read_to_string(&path)
            .map_err(|e| format!("cannot read config {}: {e}", path.display()))?;
        let cfg: Config = toml::from_str(&text)
            .map_err(|e| format!("cannot parse config {}: {e}", path.display()))?;
        Ok(cfg)
    }

    /// 平台护栏：非 Linux 或 Win11 fake 模式下，改绑回环、禁用真实探流与子进程。
    /// 这样 Win11 上 `cargo check` / `cargo run` 都不会碰到 Linux 专有能力。
    pub fn finalize(&mut self) {
        let is_linux = cfg!(target_os = "linux");
        if !is_linux || self.profile == "win11-fake" {
            self.server.bind_addr = "127.0.0.1:9090".to_string();
            self.pipeline.spawn_processes = false;
            self.mediamtx.api_enabled = false;
        }
        if self.capture.fps == 0 {
            self.capture.fps = 30; // 参数化默认值，不是承诺；实机以 --stream-count=300 为准
        }
    }

    /// GOP（key-int-max）由 capture.fps 派生，禁止独立写死 30。
    pub fn gop_frames(&self) -> u32 {
        self.capture.fps.max(1)
    }

    pub fn is_fake(&self) -> bool {
        self.profile == "win11-fake" || !self.pipeline.spawn_processes
    }
}
