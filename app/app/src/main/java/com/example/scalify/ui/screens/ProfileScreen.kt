package com.example.scalify.ui.screens

import android.annotation.SuppressLint
import android.graphics.Paint
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.paint
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.scalify.R
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun ProfilePageUI(
) {
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE8F4F0))
            .verticalScroll(scrollState)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            UserHero()
        }
    }
}

@Composable
fun UserHero() {
    val heroAreaHeightDp = 528.dp

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(heroAreaHeightDp)
    ) {
        UserHeroBg()
    }
}

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun UserHeroBg() {
    val heroAreaWidthDp = LocalConfiguration.current.screenWidthDp.dp
    Box(
        modifier = Modifier
            .fillMaxSize()
            .paint(
                painter = painterResource(R.drawable.profile_hero_texture),
                alpha = 0.05f,
                contentScale = ContentScale.FillBounds
            )
    ) {
        // gradient background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawBehind {
                    drawRect(
                        Brush.linearGradient(
                            colorStops = arrayOf(
                                0.0f to Color(0xFF0A8C6A),
                                0.48f to Color(0xFF0FB48A),
                                1f to Color(0xFF36D2A6),
                            ),
                            start = Offset(0f, 0f),
                            end = Offset(size.width, size.height)
                        )
                    )

                    drawCircle(
                        radius = size.height * 0.92f,
                        center = Offset(size.width * 0.86f, size.height * 0.12f),
                        brush = Brush.radialGradient(
                            radius = size.height * 0.92f,
                            center = Offset(size.width * 0.86f, size.height * 0.12f),
                            colorStops = arrayOf(
                                0f to Color(0xFFFFFFFF).copy(0.3f),
                                0.56f to Color.Transparent,
                            )
                        )
                    )

                    drawCircle(
                        radius = size.height * 0.84f,
                        center = Offset(size.width * 0.10f, size.height * 1.08f),
                        brush = Brush.radialGradient(
                            radius = size.height * 0.84f,
                            center = Offset(size.width * 0.10f, size.height * 1.08f),
                            colorStops = arrayOf(
                                0f to Color(0xFF000000).copy(0.2f),
                                0.60f to Color.Transparent,
                            )
                        )
                    )

                    drawCircle(
                        radius = size.height * 0.78f,
                        center = Offset(size.width * 0.80f, size.height * 0.86f),
                        brush = Brush.radialGradient(
                            radius = size.height * 0.78f,
                            center = Offset(size.width * 0.80f, size.height * 0.86f),
                            colorStops = arrayOf(
                                0f to Color(0xFF0A8C6A).copy(alpha = 0.55f),
                                0.62f to Color.Transparent,
                            )
                        )
                    )
                })

        // grid background

        Box(
            modifier = Modifier
                .fillMaxSize()
                .gridBackground()
        )

        val bubbleSizeDp = heroAreaWidthDp * 0.46f

        // animate bubble

        // 假设用 rememberInfiniteTransition 驱动
        val transition = rememberInfiniteTransition(label = "orbFloat")
        // 进度：0 ↔ 1，9s 一程，alternate 用 reverse 实现
        val progress by transition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 9000),
                repeatMode = RepeatMode.Reverse
            ),
            label = "orbFloatProgress"
        )
        Box(
            modifier = Modifier
                .size(bubbleSizeDp)
                .offset(heroAreaWidthDp * 0.64f, 162.dp)
                .graphicsLayer {
                    translationX = -12f * progress
                    scaleX = 1f + 0.06f * progress
                    scaleY = 1f + 0.06f * progress
                }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.06f),
                                Color.Black.copy(alpha = 0.03f)
                            )
                        ),
                        shape = CircleShape,
                    ),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .size(bubbleSizeDp * 0.66f)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.06f),
                                    Color.White.copy(alpha = 0.04f)
                                )
                            ),
                            shape = CircleShape
                        ),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(bubbleSizeDp * 0.66f - 24.dp)
                            .border(
                                width = 2.dp,
                                color = Color(0xFFFFFFFF).copy(0.1f),
                                shape = CircleShape
                            )
                            .drawBehind {
                                drawCircle(
                                    radius = 300f,
                                    center = Offset(x = 60f, y = 60f),
                                    brush = Brush.radialGradient(
                                        radius = 300f,
                                        center = Offset(x = 60f, y = 60f),
                                        colorStops = arrayOf(
                                            0f to Color(0xFFFFFFFF).copy(0.35f),
                                            1f to Color.Transparent
                                        )
                                    )
                                )
                            }
                    )
                }
            }
        }

        // shadow bubble
        Row(
            modifier = Modifier
                .offset(heroAreaWidthDp * 0.64f - bubbleSizeDp * 0.6f, 162.dp - bubbleSizeDp * 0.1f)
                .size(bubbleSizeDp * 1.2f)
                .border(
                    width = 1.dp,
                    color = Color.White.copy(alpha = 0.06f),
                    shape = CircleShape
                ),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .size(bubbleSizeDp * 0.9f)
                    .border(
                        width = 1.dp,
                        color = Color.White.copy(alpha = 0.06f),
                        shape = CircleShape
                    ),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .size(bubbleSizeDp * 0.6f)
                        .border(
                            width = 1.dp,
                            color = Color.White.copy(alpha = 0.06f),
                            shape = CircleShape
                        ),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(bubbleSizeDp * 0.3f)
                            .border(
                                width = 1.dp,
                                color = Color.White.copy(alpha = 0.08f),
                                shape = CircleShape
                            )
                    )
                }
            }
        }

        val sheenTransition = rememberInfiniteTransition(label = "sheen")
        // 进度：0 ↔ 1，9s 一程，alternate 用 reverse 实现
        val sheenProcess by sheenTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 9000),
                repeatMode = RepeatMode.Reverse
            ),
            label = "sheenProgress"
        )

        // highlight line
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(heroAreaWidthDp * 0.5f)
                .graphicsLayer {
                    translationX = size.width * sheenProcess
                }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .drawBehind {
                        // CSS 角度 115deg → 方向向量
                        val deg = 105.0
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
            )
        }
    }
}

fun Modifier.gridBackground(
    step: Dp = 72.dp,
    lineColor: Color = Color.White.copy(alpha = 0.05f),
    opacity: Float = 0.9f,
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
    }