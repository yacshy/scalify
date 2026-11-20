# scalp-control — Rust 控制面服务

头皮检测仪低延迟预览方案的 **Rust 控制面**。这是本轮交付的唯一代码工程（用户 11-20 收窄边界）：
**不含任何 Android / Kotlin / Java / Gradle 代码**。Android 侧如何对接，见
[`docs/ANDROID-INTEGRATION.md`](../docs/ANDROID-INTEGRATION.md)（纯协议/接口说明）。

契约权威来源：[`docs/streaming-plan.md`](../docs/streaming-plan.md) R3（唯一定稿）、
[`docs/API.md`](../docs/API.md)（端点/字段/错误码）、[`docs/openapi.yaml`](../docs/openapi.yaml)（schema）。

---

## 1. 职责边界（硬约束）

Rust **只做控制面**：

- 管：设备状态机、配置与鉴权视图、会话状态、结构化 JSONL 日志、看门狗、端侧统计反馈、
  mediamtx Control API 探流，以及通过**子进程编排** GStreamer 的启停。
- **不监听** `8889` / `8189` / `8554` / `9997`，**不接触**任何帧 / RTP / H.264 数据，
  **不实现** WebRTC、**不改**码流、**不做** RTSP→WebRTC 重封装。
- 媒体链路由 `GStreamer`（采集编码 → RTSP 环回 `127.0.0.1:8554`）+ `mediamtx`（→ WHEP）承担。
- **首版冻结码率 2500 kbps**（决策 2=A）：feedback 只上报 + 日志归因，
  响应恒 `decision="frozen"`，AIMD 类型预埋但执行器留空，Phase 2 再评估。

## 2. 目录

```
scalp-control/
├── Cargo.toml                 # 直接依赖全部 = 精确锁版本，MSRV 1.75 / edition 2021
├── config/
│   ├── default.toml           # ubuntu-server 实机（SCALP_PROFILE=server-real）
│   └── win11-fake.toml        # Win11 开发/自测（SCALP_PROFILE=win11-fake）
└── src/
    ├── main.rs                # 入口：Config → Logger → reqwest → AppState → watchdog → axum serve
    ├── config.rs              # 参数化配置 + SCALP_PROFILE + GOP 派生 + 平台护栏
    ├── state.rs               # DeviceState 状态机 + AppState(SharedState)
    ├── logging.rs             # 结构化 JSONL 追加写
    ├── feedback.rs            # StreamFeedback 校验 + FeedbackAck(frozen) + AIMD 预埋
    ├── mediamtx.rs            # Control API 探流（/v3/paths/get/scalp）
    ├── pipeline.rs            # 构造 gst-launch-1.0 argv + spawn/kill 子进程
    ├── api.rs                 # 7 个 /api/v1/* 端点，统一 {code,data,message}
    └── watchdog.rs            # 周期探流 + 掉线迁移 + 5001/5002 日志
```

## 3. Win11 上开发 / 自测（**不执行任何 Linux 专有能力**）

`config.rs::finalize()` 在「非 Linux 平台」或 `profile = "win11-fake"` 时会强制：
`bind_addr = 127.0.0.1:9090`、`spawn_processes = false`、`api_enabled = false`。
因此 Win11 上 `cargo check` / `cargo run` 都**不会**碰 `v4l2` / `GStreamer` / `hostapd`，
`pipeline::start()` 只把命令串写进日志供人工核对。

```bash
cd scalp-control
cargo check                              # 只编译，不跑
SCALP_PROFILE=win11-fake cargo run        # 起控制面，绑 127.0.0.1:9090
```

冒烟（另开一个终端）：

```bash
curl http://127.0.0.1:9090/api/v1/health
curl http://127.0.0.1:9090/api/v1/time
curl http://127.0.0.1:9090/api/v1/stream/status
curl http://127.0.0.1:9090/api/v1/config
curl -X POST http://127.0.0.1:9090/api/v1/stream/start
curl -X POST http://127.0.0.1:9090/api/v1/stream/feedback \
  -H 'Content-Type: application/json' \
  -d '{"trace_id":"<uuid>","sample_id":"<uuid>","session_seq":0,"window_ms":1000,"ts":1763600000000,"stage":"win11-fake"}'
curl -X POST http://127.0.0.1:9090/api/v1/stream/stop
```

feedback 成功响应恒为：
`{"code":0,"data":{"accepted":true,"target_kbps":2500,"decision":"frozen"},"message":""}`。

日志落在 `logs/scalp-win11-fake.jsonl`（fake）/ `logs/scalp.jsonl`（实机），一行一个 JSON。

## 4. ubuntu-server 上部署

1. 把整个 `scalp-control/` 同步到 ubuntu-server。
2. 用默认配置（`SCALP_PROFILE=server-real` 或不设，落 `default.toml`），
   确认 `10.42.0.1` 已配在 AP 接口（`docs/NETWORK-RUNBOOK.md`）。
3. `cargo build --release`，用 systemd 托管（进程自愈 `Restart=always` 由 deploy/ 提供，
   控制面自身 `watchdog.auto_restart=false`）。
4. 先跑 `deploy/preflight.sh` 实机预检（`v4l2-ctl` / `vainfo` / `gst-inspect-1.0` /
   WHEP 建连门禁 / iperf3 空口基线），确认摄像头与编码器可用后再起服务。
5. 前提：`mediamtx.yml` 的 `api: true` 才会让 Control API 探流生效；
   `api_enabled=false` 时看门狗返回 `Skipped`，不产生假告警。

> **切换只需改配置，不改代码**：Win11 → ubuntu-server 的差异全部收敛在
> `capture.device` / `encoder.name` / `network.*` / `pipeline.spawn_processes`
> 等配置字段与 `SCALP_PROFILE`。

## 5. 环境变量

| 变量 | 作用 | 默认 |
|---|---|---|
| `SCALP_CONFIG` | 显式指定配置文件绝对路径（优先级最高） | 未设 |
| `SCALP_PROFILE` | `win11-fake` → 加载 `config/win11-fake.toml`；其它值 → `default.toml` | 未设（=`default.toml`） |
| `RUST_LOG` | tracing 过滤级别，如 `RUST_LOG=scalp_control=debug,info` | `info` |

## 6. 端点速查（全部 `/api/v1/*`，绑 `10.42.0.1:9090`）

| Method | Path | 说明 | 业务码 |
|---|---|---|---|
| GET | `/api/v1/health` | 探活（`control/mediamtx/pipeline/ap`） | 0 |
| GET | `/api/v1/time` | 热点内时间同步（`server_unix_ms`） | 0 |
| GET | `/api/v1/stream/status` | 流/设备状态（重试前必查；响应 **camelCase**） | 0 |
| GET | `/api/v1/config` | 脱敏配置视图（**camelCase**，不回显口令） | 0 |
| POST | `/api/v1/stream/start` | 启采集编码管道（零自动重试） | 0 / 409→4003 |
| POST | `/api/v1/stream/stop` | 停管道（幂等） | 0 / 404→4005 |
| POST | `/api/v1/stream/feedback` | 端侧 getStats 1Hz 上报（请求体 **snake_case**） | 0 / 400→4001 |

统一响应 `{ "code": 0, "data": {}, "message": "" }`。错误码见 `docs/API.md` §4：
`4001` 字段非法 / `4003` 编码器降级或资源冲突 / `4005` 无会话 / `5001` 设备掉线 / `5002` 看门狗重启。

**两套命名域别混**：请求体 `StreamFeedback` 全 snake_case；`status` / `config` 响应全 camelCase；
`FeedbackAck.target_kbps` 与请求体 `current_target_kbps` 是两个不同字段。

## 7. P0 凭据隔离（务必遵守）

WHEP `201` 的 `ID` 响应头 = session UUID = 全链路 `trace_id`；
`Location: /scalp/whep/<secret>` 路径段 = **DELETE 能力凭据**，两者是两次独立生成的 UUID。
Rust 侧日志只接收端侧上报的 `trace_id`（来自 `ID` 头），
**绝不接触、绝不记录 `Location` / session secret**（mediamtx DELETE 无鉴权，泄露即越权）。

## 8. 首版刻意留空（非缺陷，勿误判为没做完）

- 不自动调码率：`RateController` / `AimdController` 类型预埋，`decide()` 恒 `Frozen`。
- 不自动重启管道：`watchdog.auto_restart = false`，进程自愈交 systemd。
- 不统计 WHEP reader 精确数：`stream/status.clients` 回显 runtime 值，精确计数属媒体面职责。
- 不解析 WHEP session 列表来数客户端：`mediamtx::list_sessions()` 预留但首版不在主链路调用，
  避免把 session secret 带进日志。

## 9. 实机验证清单（ubuntu-server）

- [ ] `deploy/preflight.sh` 无 FAIL（`iw list` 支持 `AP+managed total>=2`、
      `v4l2-ctl --list-formats-ext`、MJPG 各档 `--stream-count=300` 实测、`vainfo`、
      `gst-inspect-1.0 vah264enc`）。
- [ ] `cargo build --release` 通过；`SCALP_PROFILE=server-real` 起服务绑 `10.42.0.1:9090`。
- [ ] 7 个端点冒烟通过，`status` 字段与 `docs/openapi.yaml` 逐字段一致。
- [ ] feedback JSONL 与 server 侧事件同名同字段，四档 stage 归因可区分。
- [ ] 720p30 是否真有 30fps 实测确认；达不到则退 `YUYV 640×480@30` 并重算延迟预算。
- [ ] 码流验收时临时把 `maxReaders` 调到 2 或用 `tee` 旁路（否则 ffprobe/调试页占唯一 reader）。
- [ ] glass-to-glass P95 < 250 ms（单网卡 + 转发上网物理劣势），基线须在 STA 同时有上网流量时测。

## 10. 依赖锁定与版本漂移（实机 `cargo check` 踩坑记录）

**必须随仓库提交 `Cargo.lock`**（`.gitignore` 已确认不排除它）。这是本轮 2026-11-20 在 Win11 上
实机 `cargo check` 暴露的真实问题，不是理论风险：

| 项 | 现象 / 结论 |
|---|---|
| 初版 `Cargo.toml` | 所有**直接**依赖用 `=` 精确锁版本（如 `toml = "=0.8.19"`），看似求稳 |
| 首次 `cargo check` | **失败（4 × E0599）**，报错全部来自 `toml-0.8.19` 自己的 `ser.rs`，与本项目代码无关 |
| 根因 | 精确锁只管直接依赖；**传递依赖** `toml_edit` 被解析到最新 `0.22.27`，它把 `SerializeValueArray::serialize_field` / `end` 从 inherent 方法改成 trait-only，而 `toml 0.8.19` 未 `use serde::ser::SerializeTupleStruct` → 编译不过（`toml 0.8.15/0.8.19` 与新版 `toml_edit` 的已知不兼容） |
| 修复 | `toml` 升到 `=0.8.23`（0.8 系列最新补丁），并把整棵依赖树固定在 `Cargo.lock` |
| 复验 | `cargo check` **零错误零警告**（`Finished dev profile`），Win11 / fake 模式不执行任何 Linux 专有能力 |

**给 ubuntu-server 部署的硬要求**：

1. 同步代码时**带上 `Cargo.lock`**，用 `cargo build --release --locked`（`--locked` 会在锁文件与
   清单不一致时直接报错，而不是偷偷重新解析依赖）。
2. 不要在 server 上跑 `cargo update`；依赖变更先在 Win11 上 `cargo check` 通过再同步。
3. 若 server 的 Rust 工具链与 Win11 不同（如 1.75 MSRV），以 `Cargo.lock` 为准，仍用 `--locked` 构建；
   本 crate `rust-version = 1.75`，但 `Cargo.lock` 里个别传递依赖可能要求更高版本，实测为准。

> 教训：**「精确锁直接依赖」≠「构建可复现」**。只有 `Cargo.lock` + `--locked` 才能固定整棵依赖树。
