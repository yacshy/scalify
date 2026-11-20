//! 结构化 JSONL 日志。feedback 与 server 侧事件写同一份文件、同名同字段
//! （trace_id / stage / sample_id / session_seq / ts），供 P50/P95 四档归因
//! （docs/API.md §8）。
//!
//! P0 凭据隔离：日志 value 由调用方构造，本模块不接触 WHEP 的 Location /
//! session secret；trace_id 只从 201 的 ID 响应头取得。

use std::io::Write;
use std::path::PathBuf;
use std::sync::Mutex;

pub struct JsonlLogger {
    path: PathBuf,
    guard: Mutex<()>,
}

impl JsonlLogger {
    pub fn new(path: &str) -> Self {
        let path = PathBuf::from(path);
        if let Some(parent) = path.parent() {
            if let Err(e) = std::fs::create_dir_all(parent) {
                tracing::warn!(path = %parent.display(), error = %e, "cannot create log dir");
            }
        }
        Self { path, guard: Mutex::new(()) }
    }

    /// 追加一行 JSON；失败只告警、不影响控制面响应（观测是旁路，不是主链路）。
    pub fn write(&self, value: &serde_json::Value) {
        let _lock = match self.guard.lock() {
            Ok(g) => g,
            Err(poisoned) => poisoned.into_inner(),
        };
        match std::fs::OpenOptions::new().create(true).append(true).open(&self.path) {
            Ok(mut f) => {
                if let Ok(line) = serde_json::to_string(value) {
                    if let Err(e) = writeln!(f, "{line}") {
                        tracing::warn!(error = %e, "jsonl write failed");
                    }
                }
            }
            Err(e) => tracing::warn!(path = %self.path.display(), error = %e, "jsonl open failed"),
        }
    }
}
