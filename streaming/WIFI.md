# USB 无线网卡 AP 热点

在 Linux 上把 USB 无线网卡变成一个 Wi-Fi 热点（Access Point），让其他设备接入，同时通过另一张已联网的网卡（如内置 Wi-Fi）共享上网。

## 架构

```
┌──────────────────────────────────────────────────────────┐
│                    这台笔记本电脑                          │
│                                                          │
│  ┌──────────────┐    NAT/MASQUERADE    ┌──────────────┐   │
│  │ USB 无线网卡  │◄───────────────────►│ 内置 Wi-Fi    │   │
│  │  (AP 热点)    │  192.168.50.1        │ (已连外网)    │   │
│  │ wlxfc221c...  │                     │ wlp1s0       │   │
│  └──────┬───────┘                     └──────┬───────┘   │
│         │                                    │           │
│    hostapd                               192.168.1.10   │
│    dnsmasq                                (网关 192.168.1.1)
└─────────┼───────────────────────────────────────────────┘
          │
          ▼
     手机 / 平板 / 其他设备
     (通过 DHCP 拿到 192.168.50.x)
```

**关键组件：**

| 组件 | 作用 | 状态 |
|------|------|------|
| `hostapd` | 管理 AP 模式、WPA2 认证、Beacon 广播 | ✅ 已安装 |
| `dnsmasq` | DHCP + DNS（给连上的设备分 IP、提供解析） | ✅ 已安装 |
| `iptables` | NAT 伪装 + FORWARD 转发（让客户端能上网） | ✅ 系统自带 |
| `sysctl ip_forward` | 开启内核 IP 转发 | ✅ 需运行时启用 |

## 前置条件

- Linux（Debian/Ubuntu 等）
- 一张可 AP 模式的 USB 无线网卡（本项目用的是 MediaTek MT7612U，驱动 `mt76x2u`）
- 一张已经连好外网的网卡（内置 Wi-Fi 或有线）

## 一键启动脚本

把下面内容保存为 `start-ap.sh`，然后 `chmod +x start-ap.sh && sudo ./start-ap.sh`：

```bash
#!/usr/bin/env bash
set -euo pipefail

# ===================== 可配置参数 =====================
AP_IFACE="wlxfc221c4001f2"        # USB 无线网卡接口名 (iw dev 查看)
WAN_IFACE="wlp1s0"                # 已连外网的接口 (ip route show default 查看)
SSID="yacshy_wifi_test"           # 热点名称
PASSWORD="123456789"              # WPA2 密码 (至少 8 位)
AP_NET="192.168.50"               # 热点网段前缀
AP_IP="${AP_NET}.1"               # 热点本机 IP
CHANNEL=6                         # 2.4GHz 信道 (1/6/11 不重叠)
COUNTRY="CN"                      # 国家码 (影响可用信道)
TEMP_DIR="/tmp/ap"
# ====================================================

mkdir -p "$TEMP_DIR"

echo ">>> 检查依赖..."
command -v hostapd  >/dev/null || { echo "请先安装: sudo apt-get install -y hostapd dnsmasq"; exit 1; }
command -v dnsmasq  >/dev/null || { echo "请先安装: sudo apt-get install -y dnsmasq"; exit 1; }
command -v iptables >/dev/null || { echo "请先安装 iptables"; exit 1; }

echo ">>> 杀掉可能残留的旧进程..."
pkill -f "hostapd -B -P $TEMP_DIR/hostapd.pid" 2>/dev/null || true
pkill -f "dnsmasq -C $TEMP_DIR/dnsmasq.conf"   2>/dev/null || true
sleep 1

echo ">>> 配置 hostapd ($SSID @ $AP_IFACE)..."
cat > "$TEMP_DIR/hostapd.conf" <<EOF
interface=$AP_IFACE
driver=nl80211
ssid=$SSID
hw_mode=g
channel=$CHANNEL
country_code=$COUNTRY
ieee80211d=1
auth_algs=1
wpa=2
wpa_key_mgmt=WPA-PSK
wpa_passphrase=$PASSWORD
wpa_pairwise=TKIP CCMP
rsn_pairwise=CCMP
ignore_broadcast_ssid=0
EOF

echo ">>> 配置 dnsmasq (DHCP 范围 ${AP_NET}.2 ~ ${AP_NET}.254)..."
cat > "$TEMP_DIR/dnsmasq.conf" <<EOF
interface=$AP_IFACE
dhcp-range=${AP_NET}.2,${AP_NET}.254,255.255.255.0,12h
dhcp-option=3,${AP_IP}
dhcp-option=6,${AP_IP}
bind-interfaces
port=0
EOF

echo ">>> 设置 USB 网卡静态 IP ($AP_IP/24)..."
ip link set "$AP_IFACE" down
ip addr flush dev "$AP_IFACE" || true
ip addr add "$AP_IP/24" dev "$AP_IFACE"
ip link set "$AP_IFACE" up

echo ">>> 开启 IP 转发..."
sysctl -w net.ipv4.ip_forward=1

echo ">>> NAT + FORWARD 规则 (流量从 $AP_IFACE 出去伪装成 $WAN_IFACE)..."
iptables -t nat -D POSTROUTING -o "$WAN_IFACE" -j MASQUERADE 2>/dev/null || true
iptables -D FORWARD -i "$AP_IFACE" -o "$WAN_IFACE" -j ACCEPT 2>/dev/null || true
iptables -D FORWARD -i "$WAN_IFACE" -o "$AP_IFACE" -j ACCEPT 2>/dev/null || true
iptables -t nat -A POSTROUTING -o "$WAN_IFACE" -j MASQUERADE
iptables -A FORWARD -i "$AP_IFACE" -o "$WAN_IFACE" -j ACCEPT
iptables -A FORWARD -i "$WAN_IFACE" -o "$AP_IFACE" -j ACCEPT

echo ">>> 启动 dnsmasq..."
dnsmasq -C "$TEMP_DIR/dnsmasq.conf"
echo "dnsmasq PID: $!"

echo ">>> 启动 hostapd (后台)..."
hostapd -B -P "$TEMP_DIR/hostapd.pid" "$TEMP_DIR/hostapd.conf"
echo "hostapd PID: $(cat $TEMP_DIR/hostapd.pid)"

echo ""
echo "=========================================="
echo " ✅ 热点已启动！"
echo " SSID:     $SSID"
echo " 密码:     $PASSWORD"
echo " 网段:     ${AP_NET}.0/24"
echo " 网关:     $AP_IP"
echo " 出口网卡:  $WAN_IFACE"
echo "=========================================="
echo ""
echo "手机连接后访问 $AP_IP 可以看到这台电脑上跑的服务。"
echo "访问外网时流量会经过 $WAN_IFACE 出去。"
```

## 停止脚本

```bash
#!/usr/bin/env bash
set -euo pipefail

AP_IFACE="wlxfc221c4001f2"
WAN_IFACE="wlp1s0"
AP_NET="192.168.50"
AP_IP="${AP_NET}.1"

echo ">>> 停止服务..."
pkill -f "hostapd -B -P /tmp/ap/hostapd.pid" 2>/dev/null || true
pkill -f "dnsmasq -C /tmp/ap/dnsmasq.conf"   2>/dev/null || true
sleep 1

echo ">>> 清理 iptables 规则..."
iptables -t nat -D POSTROUTING -o "$WAN_IFACE" -j MASQUERADE 2>/dev/null || true
iptables -D FORWARD -i "$AP_IFACE" -o "$WAN_IFACE" -j ACCEPT 2>/dev/null || true
iptables -D FORWARD -i "$WAN_IFACE" -o "$AP_IFACE" -j ACCEPT 2>/dev/null || true

echo ">>> 关闭 IP 转发..."
sysctl -w net.ipv4.ip_forward=0

echo ">>> 清理 USB 网卡 IP..."
ip addr del "$AP_IP/24" dev "$AP_IFACE" 2>/dev/null || true
ip link set "$AP_IFACE" down

echo "✅ 热点已完全关闭。"
```

## 分步手动说明（便于理解）

以下每条命令都需要 `sudo`：

### 第 1 步：安装依赖

```bash
apt-get install -y hostapd dnsmasq
```

### 第 2 步：确认网卡接口名

```bash
iw dev                   # 列出所有无线网卡，找到 USB 那张的接口名
ip route show default    # 确认出口网卡（wlp1s0 或 enp2s0）
```

### 第 3 步：写 hostapd 配置

```bash
cat > /tmp/hostapd.conf <<'EOF'
interface=wlxfc221c4001f2       # 改成你的 USB 网卡接口名
driver=nl80211
ssid=yacshy_wifi_test
hw_mode=g
channel=6
country_code=CN
ieee80211d=1
auth_algs=1
wpa=2
wpa_key_mgmt=WPA-PSK
wpa_passphrase=123456789
wpa_pairwise=TKIP CCMP
rsn_pairwise=CCMP
EOF
```

### 第 4 步：写 dnsmasq 配置（DHCP 给客户端分 IP）

```bash
cat > /tmp/dnsmasq.conf <<'EOF'
interface=wlxfc221c4001f2
dhcp-range=192.168.50.2,192.168.50.254,255.255.255.0,12h
dhcp-option=3,192.168.50.1
dhcp-option=6,192.168.50.1
bind-interfaces
port=0
EOF
```

### 第 5 步：给 USB 网卡设静态 IP

```bash
ip link set wlxfc221c4001f2 down
ip addr flush dev wlxfc221c4001f2
ip addr add 192.168.50.1/24 dev wlxfc221c4001f2
ip link set wlxfc221c4001f2 up
```

### 第 6 步：开启 IP 转发 + NAT

```bash
# 开启内核转发
sysctl -w net.ipv4.ip_forward=1

# NAT 伪装：USB 网卡出去的流量伪装成内置 Wi-Fi 的 IP
iptables -t nat -A POSTROUTING -o wlp1s0 -j MASQUERADE

# FORWARD 放行两个方向
iptables -A FORWARD -i wlxfc221c4001f2 -o wlp1s0 -j ACCEPT
iptables -A FORWARD -i wlp1s0 -o wlxfc221c4001f2 -j ACCEPT
```

### 第 7 步：启动服务

```bash
dnsmasq -C /tmp/dnsmasq.conf &
hostapd -B -P /tmp/hostapd.pid /tmp/hostapd.conf
```

## 常见问题

### 手机能搜到热点但连不上

- 检查密码是否正确（WPA2-PSK，至少 8 位）
- `iwconfig wlxfc221c4001f2` 看有没有被 wpa_supplicant 占着（如果被占了需要先 `wpa_cli terminate`）
- 看看 hostapd 日志：`journalctl -u hostapd -f`

### 手机能连上但不能上网

- 最常见原因：**缺 IP 转发 + NAT 规则**（本项目就踩过这个坑）
- 验证转发：`cat /proc/sys/net/ipv4/ip_forward` 应该是 `1`
- 验证 NAT 规则：`iptables -t nat -L POSTROUTING -n` 应该有 MASQUERADE 条目
- 验证 FORWARD：`iptables -L FORWARD -n` 应该有两条 ACCEPT
- 客户端手动指定 DNS 试试（如 `8.8.8.8`）

### USB 网卡接口名每次都变

- 用 udev 规则固定名字：`/etc/udev/rules.d/99-usb-wifi.rules`
- 或者用 `ls /sys/class/net/wl*/device/uevent` 里看 USB 路径识别

### hostapd 启动报错 `nl80211: Driver does not support authentication/association or connect commands`

- 可能 MT7612U 当前被某个 wpa_supplicant 实例连到了（STA 模式），需要先停掉：`systemctl stop wpa_supplicant`
- 或者驱动不支持 AP 模式，试试 `iw wlxfc221c4001f2 set type ap` 手动切

### 要持久化配置（开机自启）

- hostapd 配置放到 `/etc/hostapd/hostapd.conf`，设 `systemctl enable hostapd`
- dnsmasq 配置放到 `/etc/dnsmasq.d/ap.conf`，设 `systemctl enable dnsmasq`
- IP 转发和 iptables 规则在 `/etc/rc.local` 或 `systemd-networkd` 里配置

## 硬件参考

| 项目 | 值 |
|------|-----|
| USB 无线网卡 | MediaTek MT7612U |
| USB ID | `0e8d:7612` |
| 内核驱动 | `mt76x2u` |
| 接口名 | `wlxfc221c4001f2` |
| MAC | `fc:22:1c:40:01:f2` |
| 物理端口 | `usb1/1-2/` |
