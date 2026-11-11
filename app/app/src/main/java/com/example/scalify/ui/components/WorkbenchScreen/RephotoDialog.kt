package com.example.scalify.ui.components.WorkbenchScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
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
fun RephotoDialog(
    onCloseDialog: () -> Unit,
    onRephotography: () -> Unit
) {

    val hapticClick = rememberHapticClick()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = Color(0xFF0A1F1A).copy(0.42f),
                shape = RectangleShape
            )
            .clickable(enabled = false, onClick = {})
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .align(Alignment.Center)
                .background(Color(0xFFFFFFFF)),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // 渐变装饰
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF0FB48A),
                                    Color(0xFF36D2A6)
                                )
                            )
                        )
                )

                Spacer(modifier = Modifier.height(16.dp))
                // 头部
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),

                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier
                            .weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .size(30.dp)
                                .background(
                                    shape = RoundedCornerShape(15.dp),
                                    brush = Brush.linearGradient(
                                        colors = listOf(
                                            Color(0xFF36D2A6),
                                            Color(0xFF0A8C6A)
                                        )
                                    )
                                ),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "1",
                                fontSize = 14.sp,
                                lineHeight = 14.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Serif,
                                color = Color(0xFFFFFFFF)
                            )
                        }

                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                text = "自然光",
                                fontSize = 19.sp,
                                lineHeight = 19.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Serif,
                                color = Color(0xFF0A1F1A)
                            )
                            Text(
                                modifier = Modifier.offset(x = 3.dp),
                                text = "Natural Light",
                                fontSize = 11.sp,
                                lineHeight = 11.sp,
                                fontFamily = FontFamily.SansSerif,
                                color = Color(0xFF7C948D)
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .border(width = 1.dp, color = Color(0xFFDBEAE4))
                    ) {
                        Button(
                            onClick = {
                                hapticClick()
                                onCloseDialog()
                            },
                            shape = RectangleShape,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFFFFFF),
                                contentColor = Color(0xFF0FB48A)
                            ),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.close),
                                    tint = Color(0xFF7C948D),
                                    modifier = Modifier.size(16.dp),
                                    contentDescription = null
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .background(Color.Red.copy(0.2f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 底部栏
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 0.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "点击「重拍」重新采集该光源",
                        fontSize = 12.sp,
                        lineHeight = 12.sp,
                        color = Color(0xFF7C948D),
                        fontFamily = FontFamily.SansSerif
                    )
                    Button(
                        onClick = {
                            hapticClick()
                            onRephotography()
                        },
                        contentPadding = PaddingValues(vertical = 0.dp, horizontal = 16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF0FB48A)
                        ),
                        shape = RectangleShape,
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.redo),
                                tint = Color(0xFFFFFFFF),
                                contentDescription = "重拍",
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = "重拍",
                                fontSize = 14.sp,
                                lineHeight = 14.sp,
                                color = Color(0xFFFFFFFF),
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.SansSerif
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}