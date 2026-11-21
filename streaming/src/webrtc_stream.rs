//! WebRTC 推流 pipeline
//!
//!   V4L2 MJPEG → jpeg-decoder → RGB24 → YUV420P → rusty_h264-encoder → H264 NALU → TrackLocalStaticSample

use std::{sync::Arc, time::Duration};

use async_trait::async_trait;
use rtc::media_stream::MediaStreamTrack;
use rtc::rtp_transceiver::rtp_sender::{RTCRtpCodingParameters, RTCRtpCodec, RTCRtpEncodingParameters, RtpCodecKind};
use rtc_media::Sample;
use rtc_shared::time::SystemInstant;
use tokio::{sync::mpsc, task};
use tracing::{error, info, warn};
use webrtc::{
    media_stream::{
        track_local::{static_sample::TrackLocalStaticSample, TrackLocal},
    },
    peer_connection::{
        MediaEngine,
        RTCConfigurationBuilder, RTCPeerConnectionIceEvent,
        PeerConnection, PeerConnectionBuilder, PeerConnectionEventHandler,
    },
};
use rusty_h264_common::YuvFrame;
use rusty_h264_encoder::{Encoder, EncoderConfig};

use crate::AppState;

// ———————————————— ICE forward handler ————————————————

pub struct IceForwarder {
    pub tx: tokio::sync::mpsc::UnboundedSender<String>,
}

#[async_trait]
impl PeerConnectionEventHandler for IceForwarder {
    async fn on_ice_candidate(&self, event: RTCPeerConnectionIceEvent) {
        let Ok(init) = event.candidate.to_json() else { return };
        use serde::Serialize;
        #[derive(Serialize)]
        struct Out {
            #[serde(rename = "type")] typ: &'static str,
            candidate: String,
            #[serde(rename = "sdpMid")] sdp_mid: Option<String>,
            #[serde(rename = "sdpMLineIndex")] sdp_mline_index: Option<u16>,
        }
        let msg = Out {
            typ: "ice",
            candidate: init.candidate,
            sdp_mid: init.sdp_mid,
            sdp_mline_index: init.sdp_mline_index,
        };
        if let Ok(s) = serde_json::to_string(&msg) {
            let _ = self.tx.send(s);
        }
    }
}

// ———————————————— ActiveStream ————————————————

#[derive(Clone)]
pub struct ActiveStream {
    pub pc: Arc<dyn PeerConnection>,
    pub track: Arc<TrackLocalStaticSample>,
}

unsafe impl Send for ActiveStream {}
unsafe impl Sync for ActiveStream {}

// ———————————————— PC + Track 创建 ————————————————

pub async fn create_pc_with_track(
    handler: Arc<dyn PeerConnectionEventHandler>,
) -> anyhow::Result<(Arc<dyn PeerConnection>, Arc<TrackLocalStaticSample>)> {
    // 1) 构造 MediaStreamTrack
    let media_track = MediaStreamTrack::new(
        "webrtc-stream".to_string(),
        "video-track".to_string(),
        "Camera".to_string(),
        RtpCodecKind::Video,
        vec![RTCRtpEncodingParameters {
            rtp_coding_parameters: RTCRtpCodingParameters {
                ssrc: Some(12345),
                ..Default::default()
            },
            codec: RTCRtpCodec {
                mime_type: "video/H264".to_string(),
                clock_rate: 90000,
                channels: 0,
                sdp_fmtp_line: "level-asymmetry-allowed=1;packetization-mode=1;profile-level-id=42e01f".to_string(),
                rtcp_feedback: vec![],
            },
            ..Default::default()
        }],
    );

    // 2) TrackLocalStaticSample
    let track = Arc::new(TrackLocalStaticSample::new(media_track)?);

    // 3) PeerConnection
    // 3) PeerConnection — 必须用带 default codecs 的 MediaEngine（否则 H264 不被支持）
    let mut media_engine = MediaEngine::default();
    media_engine.register_default_codecs()?;
    let pc_impl = PeerConnectionBuilder::new()
        .with_media_engine(media_engine)
        .with_configuration(RTCConfigurationBuilder::default().build())
        .with_handler(handler)
        .with_udp_addrs(vec!["0.0.0.0:0".to_string()])
        .build()
        .await?;
    let pc: Arc<dyn PeerConnection> = Arc::new(pc_impl);

    // 4) 把 track 加到 pc
    let track_dyn: Arc<dyn TrackLocal> = track.clone();
    pc.add_transceiver_from_track(track_dyn, None).await?;

    Ok((pc, track))
}

// ———————————————— RGB → YUV420P ————————————————

pub fn rgb24_to_yuv420p(rgb: &[u8], w: usize, h: usize) -> (Vec<u8>, Vec<u8>, Vec<u8>) {
    let cw = w / 2;
    let ch = h / 2;
    let mut y = vec![0u8; w * h];
    let mut u = vec![0u8; cw * ch];
    let mut v = vec![0u8; cw * ch];
    for row in 0..h {
        for col in 0..w {
            let idx = (row * w + col) * 3;
            let (r, g, b) = (rgb[idx] as i32, rgb[idx+1] as i32, rgb[idx+2] as i32);
            y[row * w + col] = ((299*r + 587*g + 114*b) / 1000).clamp(0, 255) as u8;
        }
    }
    for row in 0..ch {
        for col in 0..cw {
            let mut ur = 0i32;
            let mut ug = 0i32;
            let mut ub = 0i32;
            let (dy, dx) = (row*2, col*2);
            for (dr, dc) in [(0,0),(0,1),(1,0),(1,1)] {
                let idx = ((dy+dr)*w + (dx+dc)) * 3;
                ur += rgb[idx] as i32; ug += rgb[idx+1] as i32; ub += rgb[idx+2] as i32;
            }
            ur /= 4; ug /= 4; ub /= 4;
            let ci = row * cw + col;
            u[ci] = ((-169*ur - 331*ug + 500*ub + 128_000) / 1000).clamp(0, 255) as u8;
            v[ci] = (( 500*ur - 419*ug -  81*ub + 128_000) / 1000).clamp(0, 255) as u8;
        }
    }
    (y, u, v)
}

// ———————————————— V4L2 → 编码 → Track 主循环 ————————————————

pub fn spawn_v4l2_pipeline(state: Arc<AppState>) {
    task::spawn(async move {
        let path = match crate::v4l2_capture::find_external_camera_path() {
            Some(p) => p, None => { warn!("未找到外接 USB 摄像头"); return; }
        };
        info!(path, "WebRTC pipeline 启动");

        let capture = match task::spawn_blocking(move || {
            crate::v4l2_capture::V4l2Capture::open(&path, 1280, 720)
        }).await {
            Ok(Ok(c)) => c,
            Ok(Err(e)) => { error!(error=%e, "V4L2 open"); return; }
            Err(e) => { error!(error=%e, "spawn_blocking"); return; }
        };
        let (w, h) = (capture.width() as usize, capture.height() as usize);

        let (frame_tx, mut frame_rx) = mpsc::channel::<(Vec<u8>, u64)>(16);
        task::spawn_blocking(move || loop {
            match capture.read_frame() {
                Ok((f, pts)) => { let _ = frame_tx.try_send((f, pts)); }
                Err(e) => { error!(error=%e, "V4L2 DQBUF"); break; }
            }
        });

        let (nalu_tx, mut nalu_rx) = mpsc::channel::<(Vec<u8>, u64)>(16);
        task::spawn_blocking(move || {
            let mut enc_cfg = EncoderConfig::baseline(w, h);
            enc_cfg.framerate = 30.0;
            enc_cfg.bitrate = 2_000_000;
            enc_cfg.gop_size = 60;
            enc_cfg.qp = 26;
            let mut enc = match Encoder::new(enc_cfg) {
                Ok(e) => e, Err(e) => { error!("H264 encoder 失败: {e}"); return; }
            };
            let mut rgb = vec![0u8; w * h * 3];
            while let Some((mjpeg, pts_us)) = frame_rx.blocking_recv() {
                let mut dec = jpeg_decoder::Decoder::new(std::io::Cursor::new(&mjpeg));
                let pixels = match dec.decode() {
                    Ok(p) => p, Err(_) => continue,
                };
                if pixels.len() == w * h * 3 { rgb.copy_from_slice(&pixels); }
                else { rgb = pixels; }
                let (y, u, v) = rgb24_to_yuv420p(&rgb, w, h);
                let nalu = enc.encode(&YuvFrame { width: w, height: h, y, u, v });
                if !nalu.is_empty() { let _ = nalu_tx.blocking_send((nalu, pts_us)); }
            }
        });

        let mut frame_count = 0u64;
        let mut rtp_ts: u32 = 0;
        let frame_duration = Duration::from_secs_f64(1.0 / 30.0);
        let mut warned_no_pc = false;
        // 等 PeerConnection state = Connected 再开始推帧（DTLS 建 SRTP context 需要时间）
        while let Some((nalu, _pts_us)) = nalu_rx.recv().await {
            if !state.pc_connected.load(std::sync::atomic::Ordering::SeqCst) {
                if !warned_no_pc {
                    info!("PC 未就绪，暂不推帧（等待 connection state=Connected）");
                    warned_no_pc = true;
                }
                continue;
            }
            warned_no_pc = false;

            let guard = state.stream.lock().await;
            if let Some(active) = guard.as_ref() {
                let sample = Sample {
                    data: nalu.into(),
                    timestamp: SystemInstant::now(),
                    duration: frame_duration,
                    packet_timestamp: rtp_ts,
                    prev_dropped_packets: 0,
                    prev_padding_packets: 0,
                };
                if let Err(e) = active.track.write_sample(12345, 96, &sample, &[]).await {
                    warn!(error=%e, "track.write_sample");
                } else {
                    frame_count += 1;
                    if frame_count % 300 == 0 { info!(frame_count, "推流正常"); }
                }
                rtp_ts = rtp_ts.wrapping_add(3000); // 90kHz clock, 30fps
            }
        }
    });
}

pub async fn register_new_pc(
    state: &Arc<AppState>,
    handler: Arc<dyn PeerConnectionEventHandler>,
) -> anyhow::Result<(Arc<dyn PeerConnection>, Arc<TrackLocalStaticSample>)> {
    let (pc, track) = create_pc_with_track(handler).await?;
    let pc_clone = pc.clone();
    let track_clone = track.clone();
    let mut guard = state.stream.lock().await;
    *guard = Some(ActiveStream { pc: pc_clone, track: track_clone });
    Ok((pc, track))
}

pub async fn close_pc(state: &Arc<AppState>) {
    state.pc_connected.store(false, std::sync::atomic::Ordering::SeqCst);
    let mut guard = state.stream.lock().await;
    if let Some(active) = guard.take() {
        let _ = active.pc.close().await;
    }
}
