package com.example.hair.ui.theme

import androidx.compose.ui.graphics.Color

// ─────────────────────────────────────────────────────────────
//  Atelier 设计令牌 · 舒悦头皮护理（Scalp Atelier）v7
//  冷调薄荷白底 + 鲜亮祖母绿 (#0FB48A) + 琥珀金 视觉体系
//  与 workbench/css/atelier.css 的 :root 令牌一一对应
// ─────────────────────────────────────────────────────────────

// 表面 / 背景
val Paper      = Color(0xFFE8F4F0)   // 冷调薄荷白底
val PaperWarm  = Color(0xFFE2F0EC)
val SurfaceWhite = Color(0xFFFFFFFF) // 卡片表面（纯白）
val Surface2   = Color(0xFFF1F8F5)

// 墨色
val Ink       = Color(0xFF0A1F1A)   // 近黑墨绿，高对比
val InkSoft   = Color(0xFF3F5B54)
val InkFaint  = Color(0xFF7C948D)
val InkGhost  = Color(0xFFA8BDB7)

// 描边
val Line       = Color(0xFFDBEAE4)
val LineSoft   = Color(0xFFECF4F1)
val LineStrong = Color(0xFFC2D9D1)

// 品牌主色（鲜亮祖母绿）
val Sage       = Color(0xFF0FB48A)
val SageDeep   = Color(0xFF0A8C6A)
val SageSoft   = Color(0xFF36D2A6)

// 强调色
val AccentPurple = Color(0xFF6C5CE7)
val AccentCoral  = Color(0xFFFF7A45)
val Gold     = Color(0xFFF5A524)
val GoldSoft = Color(0xFFFFC266)

// 状态色（高饱和）
val StatusActive  = Color(0xFF0FB48A)
val StatusPending = Color(0xFFF5A524)
val StatusDone    = Color(0xFF6B8C84)
val Danger        = Color(0xFFFF4D5E)

// 半透明 tint / border（芯片 / 状态胶囊，10% / 30%）
val SageTint    = Color(0x1A0FB48A); val SageBorder    = Color(0x4D0FB48A)
val GoldTint    = Color(0x1AF5A524); val GoldBorder    = Color(0x4DF5A524)
val PurpleTint  = Color(0x1A6C5CE7); val PurpleBorder  = Color(0x4D6C5CE7)
val DangerTint  = Color(0x1AFF4D5E); val DangerBorder  = Color(0x4DFF4D5E)
val DoneTint    = Color(0x1A6B8C84); val DoneBorder    = Color(0x4D6B8C84)

// ── 登录页专用（沉浸深绿背景 + 玻璃卡）──
val DeepTeal  = Color(0xFF0A6E58)
val CyanGlow  = Color(0xFF1FA9C9)
val GlassCard = Color(0xEDFFFFFF)   // rgba(255,255,255,0.93)
val ShadowInk = Color(0x8C021E18)   // rgba(2,30,24,0.55)
