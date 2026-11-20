package com.example.scalify.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.example.scalify.R
import com.example.scalify.ui.components.common.Copyright
import com.example.scalify.ui.components.common.StepTitle
import com.example.scalify.ui.navigation.LocalNavController
import com.example.scalify.ui.navigation.NavRoutes
import com.example.scalify.ui.uitls.rememberHapticClick

@Composable
fun MajorCoursesPageUI() {
    val scrollState = rememberScrollState()


    Box(
        modifier = Modifier
            .fillMaxSize()
            .drawBehind {
                drawRect(
                    brush = Brush.radialGradient(
                        colorStops = arrayOf(
                            0f to Color(15, 180, 138).copy(alpha = 0.16f),
                            0.52f to Color.Transparent
                        ),
                        radius = size.height * 0.8f,
                        center = Offset(
                            size.width * 0.08f, size.height * -0.1f
                        ),
                    )
                )

                drawRect(
                    brush = Brush.radialGradient(
                        colorStops = arrayOf(
                            0f to Color(108, 92, 231).copy(alpha = 0.12f),
                            0.5f to Color.Transparent
                        ),
                        radius = size.height * 0.7f,
                        center = Offset(
                            size.width * 0.96f, size.height * 0.02f
                        ),
                    )
                )

                drawRect(
                    brush = Brush.radialGradient(
                        colorStops = arrayOf(
                            0f to Color(15, 180, 138).copy(alpha = 0.1f),
                            0.60f to Color.Transparent
                        ),
                        radius = size.height * 1.2f,
                        center = Offset(
                            size.width * 0.5f, size.height * 1.22f
                        ),
                    )
                )
            }
            .verticalScroll(scrollState),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp)
        ) {
            NavBackButton()
            CatHero()

            CoursesSection()

            Copyright()
        }
    }
}

@Composable
fun NavBackButton() {
    val hapticClick = rememberHapticClick()
    val navController = LocalNavController.current

    Box(
        modifier = Modifier
            .padding(top = 18.dp)
            .height(44.dp)
            .width(82.dp)
            .border(width = 1.dp, color = Color(0xFFC2D9D1))
    ) {
        Button(
            modifier = Modifier.fillMaxSize(),
            onClick = {
                hapticClick()
                navController.navigate(NavRoutes.ProfilePage)
            },
            contentPadding = PaddingValues(0.dp),
            shape = RectangleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color(0xFF0FB48A)
            )
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(
                    space = 8.dp,
                    alignment = Alignment.CenterHorizontally
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.left),
                    tint = Color(0xFF3F5B54),
                    contentDescription = "返回",
                    modifier = Modifier.size(16.dp)
                )

                Text(
                    text = "返回",
                    fontSize = 12.sp,
                    lineHeight = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif,
                    letterSpacing = 0.95.sp,
                    color = Color(0xFF3F5B54)
                )
            }
        }
    }
}

@Composable
fun CatHero() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp)
            .drawBehind {
                drawRect(
                    brush = Brush.radialGradient(
                        colorStops = arrayOf(
                            0f to Color.White.copy(0.3f),
                            0.56f to Color.Transparent
                        ),
                        radius = size.height * 0.92f,
                        center = Offset(size.width * 0.86f, size.height * 0.12f)
                    )
                )
                drawRect(
                    brush = Brush.radialGradient(
                        colorStops = arrayOf(
                            0f to Color.Black.copy(0.2f),
                            0.56f to Color.Transparent
                        ),
                        radius = size.height * 0.84f,
                        center = Offset(size.width * 0.1f, size.height)
                    )
                )
                drawRect(
                    brush = Brush.radialGradient(
                        colorStops = arrayOf(
                            0f to Color.White.copy(0.16f),
                            0.56f to Color.Transparent
                        ),
                        radius = size.height * 0.78f,
                        center = Offset(size.width * 0.8f, size.height * 0.86f)
                    )
                )

                drawRect(
                    brush = Brush.linearGradient(
                        colorStops = arrayOf(
                            0f to Color(0xFF0A8C6A),
                            0.48f to Color(0xFF0FB48A),
                            1f to Color(0xFF36D2A6)
                        ),
                    )
                )
            }
            .padding(28.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(26.dp)
                    .height(1.dp)
                    .background(Color.White)
            )

            Text(
                text = "PROFESSIONAL ACADEMY",
                fontSize = 11.sp,
                lineHeight = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif,
                color = Color.White.copy(0.86f),
                letterSpacing = 3.3.sp
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "专业课程",
                fontSize = 30.sp,
                lineHeight = 31.8.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif,
                color = Color.White,
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
        ) {
            Text(
                text = "从头皮生理认知出发，经检测指标判读、方案定制到护理实操，系统梳理门店专业服务中的关键动作与判断依据。",
                fontSize = 13.sp,
                lineHeight = 24.sp,
                fontFamily = FontFamily.SansSerif,
                color = Color.White.copy(0.92f),
            )
        }

        Column(
            modifier = Modifier
                .padding(top = 12.dp)
                .fillMaxWidth()
                .border(width = 1.dp, color = Color(0xFFFFFFFF).copy(0.46f))
                .background(Color.White.copy(0.2f))
        ) {
            HeroStats(value = "8", title = "课程")
            HeroStats(value = "29h", title = "课程时长")
            HeroStats(value = "8", title = "在学习")
            HeroStats(value = "8", title = "已完成", isShowSplitLine = false)
        }
    }
}

@Composable
fun HeroStats(
    value: String,
    title: String,
    isShowSplitLine: Boolean = true
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = value,
                    fontSize = 26.sp,
                    lineHeight = 26.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif,
                    color = Color.White,
                )
                Text(
                    text = title,
                    fontSize = 12.sp,
                    lineHeight = 21.sp,
                    fontFamily = FontFamily.SansSerif,
                    letterSpacing = 1.54.sp,
                    color = Color.White.copy(0.76f),
                )
            }
        }
        if (isShowSplitLine) {
            HorizontalDivider(thickness = 1.dp, color = Color.White.copy(0.46f))
        }
    }

}

@Composable
fun CoursesSection() {
    Column(
        modifier = Modifier
            .padding(top = 30.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        StepTitle(
            step = "1",
            title = "学习路径",
            subTitle = "LEARNING PATH"
        )
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            CourseCard()
        }
    }
}

@Composable
fun CourseCard() {
    val hapticClick = rememberHapticClick()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .dropShadow(
                shape = RectangleShape,
                shadow = Shadow(
                    radius = 2.dp,
                    color = Color(10, 31, 26).copy(0.15f)
                )
            )
            .background(Color.White)
            .padding(top = 20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .height(38.dp),
            horizontalArrangement = Arrangement.spacedBy(13.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .size(38.dp)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF36D2A6),
                                Color(0xFF0A8C6A)
                            )
                        ),
                        shape = CircleShape
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "1",
                    fontSize = 15.sp,
                    lineHeight = 15.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif,
                    color = Color.White
                )
            }
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "头皮认知",
                    fontSize = 19.sp,
                    lineHeight = 19.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif,
                    color = Color(0xFF0A1F1A)
                )
                Text(
                    text = "Basics",
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = FontFamily.Serif,
                    color = Color(0xFF7C948D)
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp, start = 20.dp, end = 20.dp)
        ) {
            Text(
                text = "先认识头皮的结构、周期与常见状态成因，知道问题出在哪儿，后面每一步才有依据。",
                fontSize = 13.sp,
                lineHeight = 22.sp,
                fontFamily = FontFamily.SansSerif,
                color = Color(0xFF3F5B54)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp, start = 20.dp, end = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "3",
                fontSize = 17.sp,
                lineHeight = 17.sp,
                fontFamily = FontFamily.Serif,
                color = Color(0xFF0A8C6A)
            )
            Text(
                text = "门课程",
                fontSize = 12.sp,
                lineHeight = 12.sp,
                fontFamily = FontFamily.Serif,
                color = Color(0xFF7C948D)
            )
            Text(
                text = "9.6",
                fontSize = 17.sp,
                lineHeight = 17.sp,
                fontFamily = FontFamily.Serif,
                color = Color(0xFF0A8C6A)
            )
            Text(
                text = "小时",
                fontSize = 11.sp,
                lineHeight = 12.sp,
                fontFamily = FontFamily.Serif,
                color = Color(0xFF7C948D)
            )
        }

        HorizontalDivider(
            thickness = 1.dp,
            color = Color(0xFFECF4F1),
            modifier = Modifier.padding(vertical = 14.dp, horizontal = 20.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(13.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .background(Color(0xFF0FB48A))
                )
                Text(
                    text = "头皮生理学基础",
                    fontSize = 13.sp,
                    lineHeight = 13.sp,
                    fontFamily = FontFamily.SansSerif,
                    color = Color(0xFF3F5B54)
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(13.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .background(Color(0xFF0FB48A))
                )
                Text(
                    text = "头皮生理学基础",
                    fontSize = 13.sp,
                    lineHeight = 13.sp,
                    fontFamily = FontFamily.SansSerif,
                    color = Color(0xFF3F5B54)
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 2.dp)
                .height(52.dp)
        ) {
            Button(
                modifier = Modifier.fillMaxSize(),
                onClick = {
                    hapticClick()
                },
                contentPadding = PaddingValues(0.dp),
                shape = RectangleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color(0xFF0FB48A)
                )

            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "查看该分类课程",
                        fontSize = 13.sp,
                        lineHeight = 13.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.SansSerif,
                        color = Color(0xFF0A8C6A)
                    )
                    Icon(
                        painter = painterResource(R.drawable.arrow_right),
                        contentDescription = "查看该分类课程",
                        tint = Color(0xFF0FB48A),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}