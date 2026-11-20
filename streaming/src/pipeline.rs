//! GStreamer 采集编码进程的「编排」（start/stop）。
//!
//! 这是控制面行为：只 spawn/kill 子进程，不读写任何帧 / RTP / H.264 数据。
//! Win11 / fake 模式下 **绝不 spawn**，只把命令字符串写进日志供人工核对。
//!
//! 硬约束（docs/API.md §3）：
//! - `key-int-max` 由 `capture.fps` 派生（Config::gop_frames），禁止写死 30；
//! - 属性名按编码器区分：vah264enc `b-frames=0` / x264enc `bframes=0` /
//!   v4l2h264enc `video_b_frames=0`；
//! - bitrate 单位：vah264enc/x264enc = kbps(2500)，v4l2h264enc = bps(2500000)；
//! - 首版冻结 2500 kbps，feedback 路径绝不修改这里的任何参数。

use serde_json::json;

use crate::state::SharedState;

/// 构造 gst-launch-1.0 的 argv（不执行）。fake profile 用 videotestsrc 假源。
pub fn build_argv(state: &SharedState) -> Vec<String> {
    let c = &state.config;
    let gop = c.gop_frames();
    let bitrate = c.encoder.bitrate_kbps;
    let mut a: Vec<String> = vec!["gst-launch-1.0".into(), "-e".into(), "-v".into()];

    if c.is_fake() {
        a.extend([
            "videotestsrc".into(), "is-live=true".into(), "!".into(),
            format!("video/x-raw,width={},height={},framerate={}/1", c.capture.width, c.capture.height, c.capture.fps),
            "!".into(),
        ]);
    } else {
        a.extend([
            "v4l2src".into(), format!("device={}", c.capture.device), "do-timestamp=true".into(), "!".into(),
            format!("image/jpeg,width={},height={},framerate={}/1", c.capture.width, c.capture.height, c.capture.fps),
            "!".into(), "jpegparse".into(), "!".into(),
            // Ubuntu 24.04 用 vajpegdec；22.04 旧名 vaapijpegdec（docs/API.md §3）
            "vajpegdec".into(), "!".into(),
            "queue".into(), "max-size-buffers=2".into(), "leaky=downstream".into(), "!".into(),
        ]);
    }

    a.extend(["videoconvert".into(), "!".into(), "video/x-raw,format=NV12".into(), "!".into()]);

    match c.encoder.name.as_str() {
        "vah264enc" => a.extend([
            "vah264enc".into(),
            format!("key-int-max={gop}"), format!("bitrate={bitrate}"),
            "rate-control=cbr".into(), "b-frames=0".into(),
            "cabac=false".into(), "dct8x8=false".into(),
        ]),
        "v4l2h264enc" => a.extend([
            "v4l2h264enc".into(),
            format!("key-int-max={gop}"), format!("video_bitrate={}", bitrate * 1000),
            "video_b_frames=0".into(),
        ]),
        _ => a.extend([
            "x264enc".into(),
            format!("key-int-max={gop}"), format!("bitrate={bitrate}"),
            "tune=zerolatency".into(), "speed-preset=ultrafast".into(),
            "bframes=0".into(), "cabac=false".into(), "dct8x8=false".into(),
            format!("option-string=min-keyint={gop}:scenecut=0:vbv-maxrate={bitrate}:vbv-bufsize={bitrate}"),
        ]),
    }

    a.extend([
        "!".into(),
        "video/x-h264,profile=baseline,stream-format=byte-stream,alignment=au".into(),
        "!".into(), "h264parse".into(), "!".into(), "rtspclientsink".into(),
        format!("location={}", c.rtsp.url), format!("protocols={}", c.rtsp.protocols),
    ]);
    a
}

/// 启动采集编码子进程。非幂等写，零自动重试（docs/API.md §7）。
pub fn start(state: &SharedState) -> Result<(), String> {
    let argv = build_argv(state);
    let cmdline = argv.join(" ");

    if !state.config.pipeline.spawn_processes {
        // Win11 fake：只记录命令串，绝不执行 Linux 专有能力。
        state.logger.write(&json!({
            "event": "pipeline_start_skipped",
            "reason": "spawn_processes=false (Win11 fake / non-Linux)",
            "stage": state.config.profile,
            "cmdline": cmdline,
            "gop_frames": state.config.gop_frames(),
            "bitrate_kbps": state.config.encoder.bitrate_kbps,
            "ts": chrono::Utc::now().timestamp_millis(),
        }));
        tracing::info!(%cmdline, "fake mode: pipeline command logged, NOT executed");
        return Ok(());
    }

    let mut guard = state.pipeline_child.lock().map_err(|e| e.to_string())?;
    if guard.is_some() {
        return Err("already running".into());
    }
    let child = std::process::Command::new(&argv[0])
        .args(&argv[1..])
        .spawn()
        .map_err(|e| format!("spawn {} failed: {e}", argv[0]))?;
    tracing::info!(pid = child.id(), "pipeline started");
    *guard = Some(child);
    state.logger.write(&json!({
        "event": "pipeline_started",
        "stage": state.config.profile,
        "gop_frames": state.config.gop_frames(),
        "bitrate_kbps": state.config.encoder.bitrate_kbps,
        "ts": chrono::Utc::now().timestamp_millis(),
    }));
    Ok(())
}

/// 停止采集编码子进程。首版不做自动重启（watchdog.auto_restart=false）。
pub fn stop(state: &SharedState) {
    let mut guard = match state.pipeline_child.lock() {
        Ok(g) => g,
        Err(p) => p.into_inner(),
    };
    if let Some(mut child) = guard.take() {
        let _ = child.kill();
        let _ = child.wait();
        tracing::info!("pipeline stopped");
    }
    state.logger.write(&json!({
        "event": "pipeline_stopped",
        "stage": state.config.profile,
        "ts": chrono::Utc::now().timestamp_millis(),
    }));
}
