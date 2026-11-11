package com.example.scalify.ui.components.WorkbenchScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scalify.R
import com.example.scalify.ui.uitls.rememberHapticClick

@Composable
fun ServiceCard() {
    val hapticClick = rememberHapticClick()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFFFFFFF))
            .drawBehind({
                drawRect(
                    color = Color(0xFF0FB48A),
                    size = Size(4.dp.toPx(), size.height)
                )
            })
    ) {
        ServiceCardHeader()

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .padding(horizontal = 20.dp)
                .background(color = Color(0xFFECF4F1))
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
        ) {
            if (Math.random() > 0.5) {
                ServiceProgress()
                ServiceProgress()
            } else {
                EmptyService()
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .padding(horizontal = 20.dp)
                .background(color = Color(0xFFECF4F1))
        )

        Button(
            onClick = {
                hapticClick()
            },
            contentPadding = PaddingValues(0.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0x00000000),
                contentColor = Color(0xFF36D2A6)
            ),
            shape = RectangleShape
        ) {
            Row(
                modifier = Modifier
                    .height(62.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "查看全部 1 项服务",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0A8C6A),
                    fontFamily = FontFamily.SansSerif
                )

                Icon(
                    painter = painterResource(R.drawable.arrow_right),
                    contentDescription = null,
                    tint = Color(0xFF0FB48A),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun ServiceCardHeader() {
    Row(
        modifier = Modifier
            .height(100.dp)
            .fillMaxWidth()
            .padding(top = 20.dp, start = 20.dp, end = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier
                .size(56.dp)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF36D2A6),
                            Color(0xFF0A8C6A)
                        )
                    ),
                    shape = RoundedCornerShape(28.dp)
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "王",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif,
                color = Color(0xFFFFFFFF)
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "王羲之",
                fontSize = 19.sp,
                color = Color(0xFF0A1F1A),
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif
            )
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(7.dp),
                verticalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                val symptoms = listOf(
                    "出油", "白发", "头皮紧绷", "脆弱细软"
                )
                symptoms.forEach { symptom ->
                    Row(
                        modifier = Modifier
                            .height(26.dp)
                            .border(
                                width = 1.dp,
                                color = Color(0xFF0FB48A).copy(0.22f),
                                shape = RoundedCornerShape(13.dp)
                            )
                            .background(
                                color = Color(0xFF0FB48A).copy(0.1f),
                                shape = RoundedCornerShape(13.dp)
                            )
                            .padding(horizontal = 10.dp)
                    ) {
                        Text(
                            text = symptom,
                            fontSize = 11.sp,
                            color = Color(0xFF0A8C6A),
                            fontWeight = FontWeight.W600,
                            fontFamily = FontFamily.SansSerif
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .height(34.dp)
                .border(
                    width = 1.dp,
                    color = Color(0xFF0FB48A).copy(0.4f),
                    shape = RoundedCornerShape(17.dp)
                )
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(Color(0xFF0FB48A), shape = RoundedCornerShape(3.dp))
            )
            Text(
                text = "2 项进行中",
                fontSize = 12.sp,
                color = Color(0xFF0FB48A)
            )
        }
    }
}

@Composable
fun ServiceProgress() {
    val hapticClick = rememberHapticClick()

    Button(
        onClick = {
            hapticClick()
        },
        contentPadding = PaddingValues(0.dp),
        shape = RectangleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0x00000000),
            contentColor = Color(0xFF36D2A6)
        )
    ) {
        Column(
            modifier = Modifier
                .height(52.dp)
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = "人参养根精华护理",
                fontWeight = FontWeight.W600,
                fontFamily = FontFamily.SansSerif,
                color = Color(0xFF0A1F1A),
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                val serviceSteps = listOf(
                    "介绍", "洗发", "上精华", "按摩", "吹干", "记录"
                )

                serviceSteps.forEach {
                    Box(
                        modifier = Modifier
                            .height(5.dp)
                            .weight(1f)
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFF0FB48A),
                                        Color(0xFF36D2A6)
                                    )
                                )
                            )
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyService() {
    val hapticClick = rememberHapticClick()

    Button(
        onClick = {
            hapticClick()
        },
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0x00FFFFFF),
            contentColor = Color(0xFF0FB48A),
        ),
        shape = RectangleShape
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "· 暂无进行中服务，点击查看全部安排",
                fontSize = 13.sp,
                fontFamily = FontFamily.SansSerif,
                color = Color(0xFF7C948D)
            )
        }
    }
}