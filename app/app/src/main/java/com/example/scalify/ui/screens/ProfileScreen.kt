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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.example.scalify.R
import com.example.scalify.ui.components.common.Copyright
import com.example.scalify.ui.components.common.QuickAccess
import com.example.scalify.ui.components.common.StepTitle
import com.example.scalify.ui.navigation.LocalNavController
import com.example.scalify.ui.navigation.NavRoutes
import com.example.scalify.ui.uitls.rememberHapticClick
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
            UserSections()
            Copyright()
        }
    }
}

@Composable
fun UserHero() {
    val heroAreaHeightDp = 480.dp

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
                .clipToBounds()
                .drawBehind {
                    drawRect(
                        Brush.linearGradient(
                            colorStops = arrayOf(
                                0.0f to Color(0xFF0A8C6A),
                                0.48f to Color(0xFF0FB48A),
                                1f to Color(0xFF36D2A6),
                            ), start = Offset(0f, 0f), end = Offset(size.width, size.height)
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

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .background(
                    brush = Brush.linearGradient(
                        colorStops = arrayOf(
                            0f to Color(0xFF0A8C6A),
                            0.38f to Color(0xFF0FB48A),
                            0.62f to Color(0xFF36D2A6),
                            1f to Color(0xFF36D2A6).copy(alpha = 0f),
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(22.dp)
        ) {
            // export id card bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(35.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.fillMaxHeight(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .height(1.dp)
                            .width(26.dp)
                            .background(Color.White)
                    )
                    Text(
                        text = "scalify",
                        fontSize = 10.sp,
                        lineHeight = 10.sp,
                        fontFamily = FontFamily.Serif,
                        fontStyle = FontStyle.Italic,
                        letterSpacing = 1.25.sp,
                        color = Color.White
                    )
                    Text(
                        text = "· MEMBER",
                        fontSize = 10.sp,
                        lineHeight = 10.sp,
                        fontWeight = FontWeight.W600,
                        fontFamily = FontFamily.SansSerif,
                        letterSpacing = 1.8.sp,
                        color = Color.White
                    )
                }
                Row(
                    modifier = Modifier
                        .height(35.dp)
                        .width(104.dp)
                        .border(
                            width = 1.dp,
                            color = Color.White.copy(alpha = 0.4f),
                            shape = RoundedCornerShape(30.dp)
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    val hapticClick = rememberHapticClick()
                    Button(
                        onClick = {
                            hapticClick()
                        },
                        modifier = Modifier.fillMaxSize(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = Color.White,
                        ),
                        contentPadding = PaddingValues(0.dp),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.spacedBy(
                                space = 10.dp, alignment = Alignment.CenterHorizontally
                            ),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.id_card),
                                tint = Color.White,
                                contentDescription = "",
                                modifier = Modifier.size(17.dp)
                            )
                            Text(
                                text = "导出名片",
                                fontSize = 12.sp,
                                lineHeight = 12.sp,
                                fontFamily = FontFamily.SansSerif,
                                fontWeight = FontWeight.W600,
                                color = Color.White
                            )
                        }
                    }
                }
            }
            // avatar bar
            Row(
                modifier = Modifier
                    .padding(top = 12.dp)
                    .size(100.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(100.dp)
                        .background(
                            color = Color.White.copy(alpha = 0.2f), shape = CircleShape
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    val avatarBoxSizeDp = 80.dp
                    Box(
                        modifier = Modifier
                            .size(avatarBoxSizeDp)
                            .background(
                                brush = Brush.radialGradient(
                                    colorStops = arrayOf(
                                        0f to Color.Transparent,
                                        0.84f to Color.Transparent,
                                        0.84f to Color.White.copy(alpha = 0.2f),
                                        0.94f to Color.White.copy(alpha = 0.2f),
                                        0.94f to Color.White.copy(alpha = 0.6f),
                                        1f to Color.White.copy(alpha = 0.6f)
                                    )
                                ), shape = CircleShape
                            )
                    ) {
                        // 图片占位

                        // edit icon
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .offset(avatarBoxSizeDp - 30.dp, avatarBoxSizeDp - 30.dp)
                                .background(
                                    brush = Brush.radialGradient(
                                        colorStops = arrayOf(
                                            0f to Color.White,
                                            0.84f to Color.White,
                                            0.84f to Color(0xFF0FB48A),
                                            1f to Color(0xFF0FB48A)
                                        )
                                    ), shape = CircleShape
                                )
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.pencil),
                                    contentDescription = "编辑",
                                    tint = Color(0xFF0FB48A),
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                        }
                    }
                }
            }

            // user name
            Row(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .fillMaxWidth()
                    .height(26.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "玲玲",
                    fontSize = 18.sp,
                    lineHeight = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif,
                    color = Color.White
                )
                Row(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(98.dp)
                        .border(
                            width = 1.dp,
                            color = Color.White.copy(alpha = 0.6f),
                            shape = CircleShape
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(
                            space = 4.dp, alignment = Alignment.CenterHorizontally
                        )
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(color = Color.White, shape = CircleShape)
                        )
                        Text(
                            text = "门店主理人",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            lineHeight = 12.sp,
                            fontFamily = FontFamily.Serif,
                            color = Color.White,
                            letterSpacing = 0.44.sp
                        )
                    }
                }
            }

            // user id
            Row(
                modifier = Modifier
                    .padding(top = 14.dp, bottom = 8.dp)
                    .fillMaxWidth()
                    .height(36.dp)
                    .border(width = 1.dp, color = Color.White.copy(0.34f), shape = CircleShape)
                    .padding(start = 14.dp, end = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(9.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.user),
                    contentDescription = "账号",
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = "账号",
                    fontSize = 12.sp,
                    lineHeight = 12.sp,
                    letterSpacing = 1.54.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif,
                    color = Color.White.copy(0.74f)
                )
                Text(
                    text = "lingling",
                    fontSize = 12.sp,
                    lineHeight = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif,
                    color = Color.White.copy(0.92f)
                )
            }

            // 店铺
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(36.dp)
                    .border(width = 1.dp, color = Color.White.copy(0.34f), shape = CircleShape)
                    .padding(start = 14.dp, end = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(9.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.shop),
                    contentDescription = "店名",
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = "店名",
                    fontSize = 12.sp,
                    lineHeight = 12.sp,
                    letterSpacing = 1.54.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif,
                    color = Color.White.copy(0.74f)
                )
                Text(
                    text = "xxxx中山店",
                    fontSize = 12.sp,
                    lineHeight = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif,
                    color = Color.White.copy(0.92f)
                )
            }

            Box(
                modifier = Modifier
                    .padding(top = 20.dp, bottom = 24.dp)
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color.White.copy(0.24f))
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(22.dp),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "12",
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif,
                    fontSize = 22.sp,
                    lineHeight = 22.sp,
                    color = Color.White
                )
                Text(
                    text = "服务中顾客",
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 11.sp,
                    lineHeight = 11.sp,
                    color = Color.White.copy(0.72f)
                )
            }

            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color.White.copy(0.24f))
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(22.dp),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "486",
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif,
                    fontSize = 22.sp,
                    lineHeight = 22.sp,
                    color = Color.White
                )
                Text(
                    text = "累计完成任务",
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 11.sp,
                    lineHeight = 11.sp,
                    color = Color.White.copy(0.72f)
                )
            }

            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color.White.copy(0.24f))
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(22.dp),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "92h",
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif,
                    fontSize = 22.sp,
                    lineHeight = 22.sp,
                    color = Color.White
                )
                Text(
                    text = "本月养护时长",
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 11.sp,
                    lineHeight = 11.sp,
                    color = Color.White.copy(0.72f)
                )
            }
        }


        val bubbleSizeDp = heroAreaWidthDp * 0.46f

        // animate bubble

        // 假设用 rememberInfiniteTransition 驱动
        val transition = rememberInfiniteTransition(label = "orbFloat")
        // 进度：0 ↔ 1，9s 一程，alternate 用 reverse 实现
        val progress by transition.animateFloat(
            initialValue = 0f, targetValue = 1f, animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 9000), repeatMode = RepeatMode.Reverse
            ), label = "orbFloatProgress"
        )
        Box(
            modifier = Modifier
                .size(bubbleSizeDp)
                .offset(heroAreaWidthDp * 0.64f, 162.dp)
                .graphicsLayer {
                    translationX = -12f * progress
                    scaleX = 1f + 0.06f * progress
                    scaleY = 1f + 0.06f * progress
                }) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.06f), Color.Black.copy(alpha = 0.03f)
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
                                    Color.White.copy(alpha = 0.06f), Color.White.copy(alpha = 0.04f)
                                )
                            ), shape = CircleShape
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
                            })
                }
            }
        }

        // shadow bubble
        Row(
            modifier = Modifier
                .offset(
                    heroAreaWidthDp * 0.64f - bubbleSizeDp * 0.6f, 162.dp - bubbleSizeDp * 0.1f
                )
                .size(bubbleSizeDp * 1.2f)
                .border(
                    width = 1.dp, color = Color.White.copy(alpha = 0.06f), shape = CircleShape
                ),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .size(bubbleSizeDp * 0.9f)
                    .border(
                        width = 1.dp, color = Color.White.copy(alpha = 0.06f), shape = CircleShape
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
            initialValue = 0f, targetValue = 1f, animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 9000), repeatMode = RepeatMode.Reverse
            ), label = "sheenProgress"
        )

        // highlight line
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(heroAreaWidthDp * 0.5f)
                .graphicsLayer {
                    translationX = size.width * sheenProcess
                }) {
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
                    })
        }
    }
}

fun Modifier.gridBackground(
    step: Dp = 72.dp,
    lineColor: Color = Color.White.copy(alpha = 0.05f),
    opacity: Float = 0.9f,
): Modifier = this.drawWithContent {
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

@Composable
fun UserSections() {
    val navController = LocalNavController.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 30.dp, start = 14.dp, end = 14.dp),
        verticalArrangement = Arrangement.spacedBy(30.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StepTitle(
                step = "01",
                title = "学习中心",
                subTitle = "COURSES & DRILLS"
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                QuickAccess(
                    icon = R.drawable.schedule,
                    title = "专业课程",
                    subTitle = "PROFESSIONAL",
                    onClick = {
                        navController.navigate(NavRoutes.MajorCoursesPage)
                    }
                )
                QuickAccess(icon = R.drawable.slide, title = "销售课程", subTitle = "SALES")
                QuickAccess(icon = R.drawable.muscle, title = "专业演练", subTitle = "PRACTICE")
                QuickAccess(
                    icon = R.drawable.sales_report,
                    title = "销售演练",
                    subTitle = "ROLE_PLAY"
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StepTitle(
                step = "02",
                title = "店铺管理",
                subTitle = "STORE MANAGEMENT"
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                QuickAccess(
                    icon = R.drawable.connection,
                    title = "服务流程编排",
                    subTitle = "WORKFLOW"
                )
                QuickAccess(
                    icon = R.drawable.shampoo_set,
                    title = "套餐管理",
                    subTitle = "PACKAGES"
                )
                QuickAccess(icon = R.drawable.shampoo, title = "单品管理", subTitle = "ITEMS")
                QuickAccess(
                    icon = R.drawable.coupon,
                    title = "促销活动管理",
                    subTitle = "PROMOTIONS"
                )
                QuickAccess(
                    icon = R.drawable.connection_point,
                    title = "下级账号管理",
                    subTitle = "SUB-ACCOUNTS"
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StepTitle(
                step = "03",
                title = "更多服务",
                subTitle = "MORE"
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                QuickAccess(
                    icon = R.drawable.schedule,
                    title = "专业课程",
                    subTitle = "PROFESSIONAL",
                    themeColor = Color(0xFFF5A524),
                    hoverColor = Color(0xFFFFC266)
                )
                QuickAccess(
                    icon = R.drawable.slide,
                    title = "销售课程",
                    subTitle = "SALES",
                    themeColor = Color(0xFFF5A524),
                    hoverColor = Color(0xFFFFC266)
                )
            }
        }
    }
}

@Composable
fun UserSectionEntry() {
}