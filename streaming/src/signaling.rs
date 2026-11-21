//! WebRTC 信令处理

use async_trait::async_trait;
use axum::extract::ws::{Message, WebSocket};
use futures::{SinkExt, StreamExt};
use serde::{Deserialize, Serialize};
use std::sync::Arc;
use tokio::sync::mpsc;
use tracing::{error, info, warn};
use webrtc::peer_connection::{
    PeerConnectionEventHandler,
    RTCPeerConnectionIceEvent,
    RTCSessionDescription,
    RTCIceCandidateInit,
};

use crate::{AppState, webrtc_stream};

#[derive(Debug, Deserialize)]
struct SigIn {
    #[serde(rename = "type")] typ: String,
    sdp: Option<String>,
    candidate: Option<String>,
    #[serde(rename = "sdpMid")] sdp_mid: Option<String>,
    #[serde(rename = "sdpMLineIndex")] sdp_mline_index: Option<u16>,
}

#[derive(Debug, Serialize)]
struct SigOut {
    #[serde(rename = "type")] typ: &'static str,
    #[serde(skip_serializing_if = "Option::is_none")] sdp: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")] candidate: Option<String>,
    #[serde(rename = "sdpMid", skip_serializing_if = "Option::is_none")] sdp_mid: Option<String>,
    #[serde(rename = "sdpMLineIndex", skip_serializing_if = "Option::is_none")] sdp_mline_index: Option<u16>,
}

struct IceBridge {
    tx: mpsc::UnboundedSender<String>,
    state: Arc<AppState>,
}

#[async_trait]
impl PeerConnectionEventHandler for IceBridge {
    async fn on_ice_candidate(&self, event: RTCPeerConnectionIceEvent) {
        let Ok(init) = event.candidate.to_json() else { return };
        // webrtc 0.20 的 to_json() 硬编码 sdp_mid="" 空串 —— 浏览器不认，手动补有效值
        let sdp_mid = init.sdp_mid.and_then(|m| if m.is_empty() { None } else { Some(m) });
        let msg = SigOut {
            typ: "ice",
            candidate: Some(init.candidate),
            sdp: None,
            sdp_mid,
            sdp_mline_index: init.sdp_mline_index,
        };
        if let Ok(s) = serde_json::to_string(&msg) {
            info!("ICE → browser: mid={:?} mline={:?} cand={}", msg.sdp_mid, msg.sdp_mline_index,
                  msg.candidate.as_deref().unwrap_or(""));
            let _ = self.tx.send(s);
        }
    }
    async fn on_connection_state_change(&self, state: webrtc::peer_connection::RTCPeerConnectionState) {
        use webrtc::peer_connection::RTCPeerConnectionState::*;
        info!(?state, "PC connection state");
        match state {
            Connected => self.state.pc_connected.store(true, std::sync::atomic::Ordering::SeqCst),
            Closed | Failed | Disconnected => self.state.pc_connected.store(false, std::sync::atomic::Ordering::SeqCst),
            _ => {}
        }
    }
}

pub async fn handle_signaling(socket: WebSocket, state: Arc<AppState>) {
    info!("WebRTC signaling ws connected");

    let (mut ws_sender, mut ws_receiver) = socket.split();
    let (ice_tx, mut ice_rx) = mpsc::unbounded_channel::<String>();

    let handler: Arc<dyn PeerConnectionEventHandler> = Arc::new(IceBridge { tx: ice_tx, state: state.clone() });
    let pc = match webrtc_stream::register_new_pc(&state, handler).await {
        Ok((pc, _track)) => pc,
        Err(e) => { error!(error=%e, "PC 创建失败"); let _ = ws_sender.close().await; return; }
    };

    loop {
        tokio::select! {
            biased;

            ice_msg = ice_rx.recv() => {
                match ice_msg {
                    Some(s) => {
                        if ws_sender.send(Message::Text(s.into())).await.is_err() { break; }
                    }
                    None => break,
                }
            }

            ws_msg = ws_receiver.next() => {
                let Some(msg) = ws_msg else { break };
                let text = match msg {
                    Ok(Message::Text(t)) => t,
                    Ok(Message::Close(_)) => break,
                    Ok(_) => continue,
                    Err(e) => { error!(error=%e, "signaling ws err"); break; }
                };
                let sig: SigIn = match serde_json::from_str(&text) {
                    Ok(s) => s, Err(e) => { warn!(error=%e, "JSON 解析失败"); continue; }
                };

                match sig.typ.as_str() {
                    "offer" => {
                        let sdp = match sig.sdp { Some(s) => s, None => { warn!("offer 缺 sdp"); continue; } };
                        info!("收到 offer (len={})\n{}", sdp.len(), sdp);
                        let offer_desc = match RTCSessionDescription::offer(sdp) {
                            Ok(d) => d, Err(e) => { error!("构造 offer: {e}"); break; }
                        };
                        if let Err(e) = pc.set_remote_description(offer_desc).await {
                            error!(error=%e, "set_remote_description"); break;
                        }
                        let answer = match pc.create_answer(None).await {
                            Ok(a) => a, Err(e) => { error!(error=%e, "create_answer"); break; }
                        };
                        let answer_sdp = answer.sdp.clone();
                        if let Err(e) = pc.set_local_description(answer).await {
                            error!(error=%e, "set_local_description"); break;
                        }
                        let msg = SigOut {
                            typ: "answer", sdp: Some(answer_sdp),
                            candidate: None, sdp_mid: None, sdp_mline_index: None,
                        };
                        if let Ok(s) = serde_json::to_string(&msg) {
                            let _ = ws_sender.send(Message::Text(s.into())).await;
                        }
                    }
                    "ice" => {
                        let cand_str = match sig.candidate { Some(c) => c, None => continue };
                        let init = RTCIceCandidateInit {
                            candidate: cand_str,
                            sdp_mid: sig.sdp_mid,
                            sdp_mline_index: sig.sdp_mline_index,
                            username_fragment: None,
                            url: None,
                        };
                        if let Err(e) = pc.add_ice_candidate(init).await {
                            warn!(error=%e, "add_ice_candidate");
                        }
                    }
                    _ => warn!(typ=sig.typ, "未知消息类型"),
                }
            }
        }
    }

    webrtc_stream::close_pc(&state).await;
    info!("signaling ws disconnected");
}
