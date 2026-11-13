package com.example.scalify.ui.components.LoginScreen

import android.annotation.SuppressLint
import android.graphics.Paint
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

fun Modifier.gridBackground(
    step: Dp = 72.dp,
    lineColor: Color = Color.White.copy(alpha = 0.07f),
    opacity: Float = 0.42f,
    maskCenter: Offset = Offset(0.60f, 0.30f),
    maskStart: Float = 0.38f,
    maskEnd: Float = 0.80f
): Modifier = this
    .drawWithContent {
        drawContent()

        val w = size.width
        val h = size.height
        val stepPx = step.toPx()
        val lineWidthPx = 1.dp.toPx()
        val paint = Paint().apply {
            isAntiAlias = true
            color = lineColor.copy(alpha = lineColor.alpha * opacity).toArgb()
            strokeWidth = lineWidthPx
        }
        val nc = drawContext.canvas.nativeCanvas

        // 垂直线
        var x = 0f
        while (x <= w) {
            nc.drawLine(x, 0f, x, h, paint)
            x += stepPx
        }
        // 水平线
        var y = 0f
        while (y <= h) {
            nc.drawLine(0f, y, w, y, paint)
            y += stepPx
        }

        // 径向遮罩
        drawRect(
            brush = Brush.radialGradient(
                colorStops = arrayOf(
                    maskStart to Color.Black,
                    maskEnd to Color.Transparent
                ),
                center = Offset(w * maskCenter.x, h * maskCenter.y),
                radius = maxOf(w * 1.25f, h * 1.10f)
            ),
            blendMode = BlendMode.DstIn
        )
    }

@SuppressLint("ConfigurationScreenWidthHeight", "UnusedBoxWithConstraintsScope")
@Composable
fun AuthOrb(
    modifier: Modifier,
    easing: Easing = CubicBezierEasing(0.4f, 0.0f, 1.0f, 1.0f),
    durationMillis: Int = 6000,
) {
    BoxWithConstraints(modifier = modifier) {
        val screenWidthDp = LocalConfiguration.current.screenWidthDp

        val transition = rememberInfiniteTransition(label = "orbFloat")
        val progress by transition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis, easing = easing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "orbFloatProgress"
        )

        val translateY = -26f * progress
        val scale = 1f + 0.06f * progress

        Box(
            modifier = Modifier
                .size(200.dp)
                .align(Alignment.TopEnd)
                .offset(
                    x = 40.dp,
                    y = -50.dp
                )
                .graphicsLayer {
                    translationY = translateY
                    scaleX = scale
                    scaleY = scale
                }
                .drawBehind {
                    drawOrbGlow(200f)
                }
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        center = Offset(130f, 200f),
                        radius = 260f,
                        colors = listOf(
                            Color.White.copy(alpha = 0.4f),
                            Color(0xF00A6E58).copy(alpha = 0.15f)
                        )
                    )
                )
        )
    }
}

/**
 * 模拟 box-shadow: 0 0 120px 36px rgba(255,255,255,0.22)
 * Compose 的 Modifier.shadow 不支持无偏移模糊光晕 + 自定义颜色，
 * 所以用径向渐变绘制在圆背后。
 */
private fun DrawScope.drawOrbGlow(orbSizePx: Float) {
    val blurPx = 120f
    val spreadPx = 36f
    val glowRadius = orbSizePx / 2f + spreadPx + blurPx
    val center = Offset(size.width / 2f, size.height / 2f)

    drawCircle(
        brush = Brush.radialGradient(
            colorStops = arrayOf(
                // 内圈到实体边缘保持实色
                0f to Color.White.copy(alpha = 0.22f),
                (orbSizePx / 2f + spreadPx) / glowRadius to Color.White.copy(alpha = 0.22f),
                // 向外模糊衰减到透明
                1f to Color.White.copy(alpha = 0f),
            ),
            center = center,
            radius = glowRadius
        ),
        radius = glowRadius,
        center = center
    )
}

@Composable
fun AuthSheen(
    duration: Int
) {
    val transition = rememberInfiniteTransition(label = "sheen")

    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = duration
                -0.32f at 0 using EaseInOut
                0.32f at duration using EaseInOut
            },
            repeatMode = RepeatMode.Reverse
        ),
        label = "sheenProgress"
    )

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        // translationX 是相对元素自身宽度的百分比
        val widthPx = constraints.maxWidth.toFloat()

        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    translationX = progress * widthPx
                }
                .drawBehind {
                    drawSheen()
                }
        )
    }
}

/**
 * 模拟 linear-gradient(115deg, transparent 38%, rgba(255,255,255,0.16) 50%, transparent 62%)
 */
private fun DrawScope.drawSheen() {
    // CSS 角度 115deg → 方向向量
    val deg = 115.0
    val rad = Math.toRadians(deg)
    val dx = sin(rad).toFloat()   // ≈ 0.9063
    val dy = -cos(rad).toFloat()  // ≈ 0.4226

    val w = size.width
    val h = size.height
    val center = Offset(w / 2f, h / 2f)

    // 让渐变线足够长以覆盖整个元素
    val halfLen = (abs(dx) * w + abs(dy) * h) / 2f
    val start = Offset(center.x - dx * halfLen, center.y - dy * halfLen)
    val end = Offset(center.x + dx * halfLen, center.y + dy * halfLen)

    drawRect(
        brush = Brush.linearGradient(
            colorStops = arrayOf(
                0.38f to Color.Transparent,
                0.50f to Color.White.copy(alpha = 0.12f),
                0.62f to Color.Transparent,
            ),
            start = start,
            end = end,
        )
    )
}

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun LoginPageBackground(
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // 渐变背景
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawBehind {
                    val w = size.width
                    val h = size.height

                    // ---- 第 3 层（最底）：linear-gradient(158deg, #0a6e58, #0fb48a 46%, #1fa9c9) ----
                    // 158deg 换算：CSS 角度以「向上为 0，顺时针」计算
                    // 158° 对应方向向量 (sin158°, -cos158°) ≈ (0.375, 0.927)
                    val angleRad = Math.toRadians(158.0)
                    val dx = sin(angleRad).toFloat()
                    val dy = -cos(angleRad).toFloat()
                    // 用元素对角线长度保证渐变覆盖整个区域
                    val len = hypot(w.toDouble(), h.toDouble()).toFloat()
                    val cx = w / 2f
                    val cy = h / 2f
                    drawRect(
                        brush = Brush.linearGradient(
                            colorStops = arrayOf(
                                0f to Color(0xFF0A6E58),
                                0.46f to Color(0xFF0FB48A),
                                1f to Color(0xFF1FA9C9)
                            ),
                            start = Offset(cx - dx * len / 2f, cy - dy * len / 2f),
                            end = Offset(cx + dx * len / 2f, cy + dy * len / 2f)
                        )
                    )

                    // ---- 第 1 层：radial-gradient(72% 92% at 84% 10%, rgba(54,210,166,0.55), transparent 54%) ----
                    drawRect(
                        brush = Brush.radialGradient(
                            colorStops = arrayOf(
                                0f to Color(0x8C36D2A6),      // 0.55 alpha ≈ 0x8C
                                0.54f to Color.Transparent
                            ),
                            center = Offset(w * 0.84f, h * 0.10f),
                            radius = maxOf(w * 0.72f, h * 0.92f) // 近似：不支持椭圆，取较大值
                        )
                    )

                    // ---- 第 2 层：radial-gradient(62% 84% at 10% 94%, rgba(126,92,224,0.46), transparent 56%) ----
                    drawRect(
                        brush = Brush.radialGradient(
                            colorStops = arrayOf(
                                0f to Color(0x757E5CE0),      // 0.46 alpha ≈ 0x75
                                0.56f to Color.Transparent
                            ),
                            center = Offset(w * 0.10f, h * 0.94f),
                            radius = maxOf(w * 0.62f, h * 0.84f)
                        )
                    )
                }
        )
        // 网格
        Box(
            modifier = Modifier
                .fillMaxSize()
                .gridBackground()
        )
        // 右上角 bubble
        Box(
            modifier = Modifier
                .size(300.dp)
                .align(Alignment.TopEnd)
        ) {
            AuthOrb(Modifier.fillMaxSize())
        }
        // 标题字
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = "SCALIFY",
                fontSize = 120.sp,
                lineHeight = 120.sp,
                fontWeight = FontWeight.W600,
                color = Color(0x18FFFFFF),
                fontFamily = FontFamily.Serif,
                overflow = TextOverflow.Visible,
                maxLines = 1,
                modifier = Modifier
                    .height(120.dp)
                    .align(Alignment.BottomStart)
                    .offset(y = 40.dp),
            )
        }
        // 光晕
        Box(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center)
        ) {
            AuthSheen(8000)
        }
        // 内容插槽
        val mainContainerPaddingX = 16.dp
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = mainContainerPaddingX, end = mainContainerPaddingX),
            verticalArrangement = Arrangement.Center
        ) {
            content()
        }
    }
}

@Preview
@Composable
fun LoginPageBackgroundPreview() {
    LoginPageBackground(
        content = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0x40FFFFFF))
            )
        }
    )
}