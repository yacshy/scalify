//! 端侧 getStats 上报：接收 + 校验 + JSONL 落盘 + 日志归因。
//!
//! 决策 2 = A（首版冻结码率）：响应恒 `decision="frozen"`、`target_kbps=2500`。
//! **绝不自动调码率、绝不改任何编码器参数**。AIMD 状态机的类型/字段预埋，
//! 但执行器留空且不可被调用（Phase 2 再评估）。
//!
//! 命名域：请求体全 snake_case（openapi.yaml StreamFeedback）。
//! `GET /api/v1/stream/status` 响应是 camelCase，见 api.rs，两者不共用 struct。

use serde::{Deserialize, Serialize};

pub const FROZEN_TARGET_KBPS: u32 = 2500;

pub const STAGES: [&str; 4] = ["win11-fake", "server-fake", "server-real", "server-yuyv"];

#[derive(Debug, Clone, Deserialize)]
pub struct StreamFeedback {
    pub trace_id: String,
    pub sample_id: String,
    pub session_seq: u64,
    pub window_ms: u64,
    pub ts: i64,
    pub stage: String,
    #[serde(default)] pub packets_lost: i64,
    #[serde(default)] pub nack_count: i64,
    #[serde(default)] pub pli_count: i64,
    #[serde(default)] pub retransmitted_packets_received: i64,
    #[serde(default)] pub frames_decoded: i64,
    #[serde(default)] pub loss_ratio: f64,
    #[serde(default)] pub jitter_buf_ms: f64,
    #[serde(default)] pub decode_ms: f64,
    #[serde(default)] pub decode_ms_p95: f64,
    #[serde(default)] pub e2e_ms_p50: f64,
    #[serde(default)] pub e2e_ms_p95: f64,
    #[serde(default)] pub current_target_kbps: u32,
    #[serde(default)] pub freeze_count: i64,
    #[serde(default)] pub app_version: String,
}

impl StreamFeedback {
    /// 校验失败返回业务码 4001 的 message。不把累计值「转换」成增量——那是端侧职责；
    /// 这里只拒绝明显非法的输入，并在日志里标注口径。
    pub fn validate(&self) -> Result<(), String> {
        if self.trace_id.trim().is_empty() {
            return Err("trace_id required (= WHEP 201 ID header; never the Location secret)".into());
        }
        if self.sample_id.trim().is_empty() {
            return Err("sample_id required".into());
        }
        if self.window_ms == 0 {
            return Err("window_ms must be > 0".into());
        }
        if self.ts <= 0 {
            return Err("ts must be a positive Unix ms (calibrate via /api/v1/time)".into());
        }
        if !STAGES.contains(&self.stage.as_str()) {
            return Err(format!("stage must be one of {}", STAGES.join("|")));
        }
        // D5=A 下 nack_count 允许非零，不做「必须 0」的校验。
        for (name, v) in [
            ("packets_lost", self.packets_lost),
            ("nack_count", self.nack_count),
            ("pli_count", self.pli_count),
            ("retransmitted_packets_received", self.retransmitted_packets_received),
            ("frames_decoded", self.frames_decoded),
            ("freeze_count", self.freeze_count),
        ] {
            if v < 0 {
                return Err(format!("{name} is a window delta, must be >= 0"));
            }
        }
        for (name, v) in [
            ("loss_ratio", self.loss_ratio),
            ("jitter_buf_ms", self.jitter_buf_ms),
            ("decode_ms", self.decode_ms),
            ("decode_ms_p95", self.decode_ms_p95),
            ("e2e_ms_p50", self.e2e_ms_p50),
            ("e2e_ms_p95", self.e2e_ms_p95),
        ] {
            if !v.is_finite() || v < 0.0 {
                return Err(format!("{name} must be finite and >= 0"));
            }
        }
        if self.loss_ratio > 1.0 {
            return Err("loss_ratio must be <= 1.0".into());
        }
        Ok(())
    }
}

#[derive(Debug, Clone, Copy, Serialize)]
pub struct FeedbackAck {
    pub accepted: bool,
    pub target_kbps: u32,
    pub decision: &'static str,
}

impl FeedbackAck {
    /// 首版唯一允许的构造：冻结 2500 kbps。
    pub fn frozen() -> Self {
        Self { accepted: true, target_kbps: FROZEN_TARGET_KBPS, decision: "frozen" }
    }
}

// ---- Phase 2 预埋：AIMD 状态机类型与 trait，执行器留空 ----------------------
// 首版不实例化、不注册、不在任何 handler 里调用。保留类型只为将来不必改契约。
#[allow(dead_code)]
#[derive(Debug, Clone, Copy)]
pub enum RateDecision {
    Frozen,
    Decrease(u32),
    Increase(u32),
}

#[allow(dead_code)]
pub trait RateController: Send + Sync {
    fn decide(&self, _sample: &StreamFeedback, _current_kbps: u32) -> RateDecision {
        RateDecision::Frozen
    }
}

#[allow(dead_code)]
pub struct AimdController {
    pub min_kbps: u32,
    pub max_kbps: u32,
    pub decrease_factor: f64,
    pub increase_step_kbps: u32,
    pub cooldown_ms: u64,
}

#[allow(dead_code)]
impl RateController for AimdController {
    // 执行器留空：默认 trait 实现恒 Frozen。Phase 2 评估后再实现真实 AIMD，
    // 且执行点只能在 GStreamer encoder element（mediamtx 是路由器，不编码）。
}
