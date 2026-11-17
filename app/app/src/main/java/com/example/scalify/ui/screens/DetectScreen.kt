package com.example.scalify.ui.screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scalify.R
import com.example.scalify.ui.components.WorkbenchScreen.RephotoDialog
import com.example.scalify.ui.navigation.LocalNavController
import com.example.scalify.ui.uitls.rememberHapticClick

data class ThumbnialItem(val index: Int, val name: String, val photo: String)

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun DetectPageUI() {

    val hapticClick = rememberHapticClick()
    val navController = LocalNavController.current

    var isShowRephotoDialog by remember { mutableStateOf(false) }

    fun onToggleRephotoDialog(newV: Boolean) {
        isShowRephotoDialog = newV
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .blur(if (isShowRephotoDialog) 10.dp else 0.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 12.dp, top = 12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(shape = RectangleShape)
                            .border(width = 1.dp, color = Color(0xFFFFFFFF).copy(0.22f))
                    ) {
                        Button(
                            onClick = {
                                hapticClick()
                                navController.popBackStack()
                            },
                            contentPadding = PaddingValues(0.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0x8C0A1F1A)
                            ),
                            shape = RectangleShape,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.left),
                                tint = Color(0xFFFFFFFF),
                                contentDescription = "返回",
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
                ScanArea()
            }
            BottomBar(
                onPreviewThumbnail = {
                    onToggleRephotoDialog(true)
                    Log.e("test", "test 12")
                })
        }
        if (isShowRephotoDialog) {
            RephotoDialog(onCloseDialog = {
                onToggleRephotoDialog(false)
            }, onRephotography = {
                onToggleRephotoDialog(false)
            })
        }
    }
}


@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun ScanArea() {
    val screenWidth = LocalConfiguration.current.screenWidthDp
    val screenHeight = LocalConfiguration.current.screenHeightDp

    val focusAreaSpace = 8f
    val focusAreaSize = screenWidth - focusAreaSpace * 2

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {

        val highlightAreaOffset =
            Offset(x = focusAreaSpace, y = (screenHeight - 88 - focusAreaSize) / 2)

        val density = LocalDensity.current
        val focusPx = with(density) { focusAreaSize.dp.toPx() }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    compositingStrategy = CompositingStrategy.Offscreen
                }
                .drawWithContent {
                    drawContent()
                    // 画半透明遮罩
                    drawRect(color = Color(0xFF07140F).copy(alpha = 0.3f))
                    // 挖出取景框区域
                    drawRect(
                        color = Color.Transparent, topLeft = Offset(
                            highlightAreaOffset.x.dp.toPx(), highlightAreaOffset.y.dp.toPx()
                        ), size = Size(focusPx, focusPx), blendMode = BlendMode.Clear
                    )
                })
    }
}

@Composable
fun BottomBar(
    onPreviewThumbnail: (photo: ThumbnialItem) -> Unit
) {

    val hapticClick = rememberHapticClick()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(88.dp)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF0A1F1A), Color(0xFF07140F)
                    )
                )
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val thumbnialItems = listOf<ThumbnialItem>(
                    ThumbnialItem(index = 1, name = "自然光", photo = ""),
                    ThumbnialItem(index = 2, name = "偏振光", photo = ""),
                    ThumbnialItem(index = 3, name = "UV光", photo = ""),
                )

                thumbnialItems.forEach { item ->
                    Thumbnail(item, { it -> onPreviewThumbnail(it) })
                }
            }
            Row(
                modifier = Modifier.fillMaxHeight(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        hapticClick()
                    },
                    shape = RectangleShape,
                    contentPadding = PaddingValues(0.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF0FB48A),
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.upload_one),
                            tint = Color(0xFFFFFFFF),
                            modifier = Modifier.size(14.dp),
                            contentDescription = "upload"
                        )
                        Text(
                            text = "上传",
                            fontSize = 12.sp,
                            lineHeight = 12.sp,
                            color = Color(0xFFFFFFFF),
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.SansSerif
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Thumbnail(thumbnial: ThumbnialItem, onPreiew: (thumbnial: ThumbnialItem) -> Unit) {

    val hapticClick = rememberHapticClick()

    Column(
        modifier = Modifier
            .width(50.dp)
            .background(Color(0xFFFFFFFF).copy(0.08f))
            .border(width = 1.dp, color = Color(0xFFFFFFFF).copy(0.16f))
            .padding(5.dp)
            .clickable(onClick = {
                hapticClick()
                onPreiew(thumbnial)
            }),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Box(
            modifier = Modifier.size(40.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFFE9F4F0), Color(0xFFD7ECE4)
                            )
                        )
                    )
            )
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .offset(x = 20.dp, y = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            color = Color(0xFF0FB48A), shape = RoundedCornerShape(8.dp)
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.check_small),
                        tint = Color(0xFFFFFFFF),
                        contentDescription = null,
                        modifier = Modifier.size(11.dp)
                    )
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = thumbnial.name,
                fontSize = 10.sp,
                lineHeight = 10.sp,
                color = Color(0xFFEAF7F2),
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif
            )
        }
    }
}
