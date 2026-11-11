package com.example.scalify.ui.components.LoginScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.example.scalify.R
import com.example.hair.ui.theme.Ink
import com.example.hair.ui.theme.InkFaint
import com.example.hair.ui.theme.Sage
import com.example.hair.ui.theme.SageDeep


@Preview
@Composable
fun LoginPageBaseForm() {
    FormContaner {
        BrandMark()
        var currentTab by remember { mutableStateOf(CurrentTab.REGISTER) }

        WelcomeTitle(title = if (currentTab == CurrentTab.PASSWORD || currentTab == CurrentTab.VERIFYCODE) "LOG IN" else "CREATE ACCOUNT")

        if (currentTab == CurrentTab.PASSWORD || currentTab == CurrentTab.VERIFYCODE) {
            TabBar(
                currentTab = currentTab,
                onTabChange = { currentTab = it }
            )
        }
        if (currentTab == CurrentTab.PASSWORD || currentTab == CurrentTab.VERIFYCODE) {
            LoginPageLoginForm(
                currentTab = currentTab,
                onClickRegister = { currentTab = it }
            )
        } else if (currentTab == CurrentTab.REGISTER) {
            LoginPageRegisterForm(
                onClickLogin = { currentTab = it }
            )
        }
    }
}

/** 表单卡片容器 */
@Composable
fun FormContaner(
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            // 背景
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        color = Color.White.copy(alpha = 0.93f),
                        shape = RoundedCornerShape(0.dp) // 或你的圆角
                    )
                    .border(
                        width = 1.dp,
                        color = Color.White.copy(alpha = 0.65f),
                        shape = RoundedCornerShape(0.dp)
                    )
                    .blur(22.dp) // ⚠️ 见下方说明
                    .shadow(
                        elevation = 32.dp,
                        shape = RoundedCornerShape(0.dp),
                        ambientColor = Color(0xFF021E18).copy(alpha = 0.55f),
                        spotColor = Color(0xFF021E18).copy(alpha = 0.55f)
                    )
            )


            val titleLinePaddingX = 1.dp
            val titleLinePaddingY = 2.dp
            val titleLineHeight = 3.dp
            Box(
                modifier = Modifier
                    .padding(
                        top = titleLinePaddingY, start = titleLinePaddingX, end = titleLinePaddingX
                    )
                    .height(titleLineHeight)
                    .fillMaxWidth()
                    .background(
                        brush = Brush.horizontalGradient(
                            colorStops = arrayOf(
                                0.00f to Color(0xFF0FB48A),
                                0.52f to Color(0xFF36D2A6),
                                1.00f to Color(0xFF1FA9C9)
                            )
                        ), shape = RectangleShape
                    )
            )

            val titleLineSpace = titleLinePaddingY + titleLineHeight
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = titleLineSpace)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 18.dp, end = 18.dp, top = 15.dp, bottom = 18.dp),
                ) {
                    content()
                }
            }
        }
    }
}

/** 品牌锚：圆形徽标 + 中英文名称 */
@Composable
fun BrandMark() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(33.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(33.dp)
                .shadow(
                    6.dp,
                    CircleShape,
                    ambientColor = SageDeep.copy(alpha = 0.32f),
                    spotColor = SageDeep.copy(alpha = 0.32f)
                )
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(Sage, SageDeep))),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.logo),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(19.dp)
            )
        }
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(start = 10.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "头皮养护助手",
                fontSize = 16.sp,
                lineHeight = 16.sp,
                fontWeight = FontWeight.W600,
                fontFamily = FontFamily.Serif,
                style = MaterialTheme.typography.titleMedium,
                letterSpacing = 0.01f.em,
                color = Color(0xFF0A1F1A)
            )
            Text(
                text = "scalify",
                fontSize = 9.sp,
                lineHeight = 9.sp,
                letterSpacing = 0.1f.em,
                color = Color(0xFF0A8C6A),
                fontStyle = FontStyle.Italic,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.W600
            )
        }
    }
}

/** 欢迎标题 */
@Composable
fun WelcomeTitle(
    title: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 13.dp, bottom = 10.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                color = Color(0xFF0A8C6A),
                fontSize = 11.sp,
                lineHeight = 11.sp
            )
        }
    }
}

enum class CurrentTab {
    PASSWORD, VERIFYCODE, REGISTER
}

/** tab bar */
@Composable
fun TabBar(
    currentTab: CurrentTab,
    onTabChange: (tab: CurrentTab) -> Unit
) {
    val tabContainerModify = Modifier
        .fillMaxHeight()
        .padding(3.dp)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(37.dp)
            .background(Color(0xFFF1F8F5))
            .border(
                width = 1.dp, color = Color(0xFFDBEAE4)
            )
    ) {
        Box(
            modifier = tabContainerModify
                .weight(1f)
                .background(
                    color = if (currentTab == CurrentTab.PASSWORD) Color(0xFFFFFFFF) else Color(
                        0x00FFFFFF
                    )
                )
                .clickable(onClick = { onTabChange(CurrentTab.PASSWORD) })
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Box() {
                    Text(
                        text = "密码登录",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF0A1F1A)
                    )
                }
            }
        }

        Box(
            modifier = tabContainerModify
                .weight(1f)
                .background(
                    color = if (currentTab == CurrentTab.VERIFYCODE) Color(0xFFFFFFFF) else Color(
                        0x00FFFFFF
                    )
                )
                .clickable(onClick = { onTabChange(CurrentTab.VERIFYCODE) })
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Box() {
                    Text(
                        text = "验证码登录",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF3F5B54)
                    )
                }
            }
        }
    }
}