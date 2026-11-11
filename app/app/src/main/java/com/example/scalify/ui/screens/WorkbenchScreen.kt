package com.example.scalify.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scalify.R
import com.example.scalify.ui.components.WorkbenchScreen.Advertisement
import com.example.scalify.ui.components.WorkbenchScreen.CreateService
import com.example.scalify.ui.components.WorkbenchScreen.ServiceCard
import com.example.scalify.ui.components.WorkbenchScreen.StepTitle
import com.example.scalify.ui.theme.CormorantGaramondItalicFamily
import com.example.scalify.ui.uitls.rememberHapticClick

@Composable
fun WorkbenchPageUI() {
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE8F4F0))
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)

        ) {
            Advertisement()

            Spacer(modifier = Modifier.height(36.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
            ) {
                val spaceLineModify = Modifier
                    .width(64.dp)
                    .height(1.dp)
                    .background(Color(0xFFC2D9D1))
                Row(
                    modifier = Modifier.fillMaxHeight(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = spaceLineModify
                    )
                    Text(
                        text = "快速导航 · QUICK ACCESS",
                        fontSize = 11.sp,
                        color = Color(0xFF7C948D),
                    )
                    Box(
                        modifier = spaceLineModify
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 14.dp, end = 14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                data class AccessItem(val title: String, val subTitle: String, val icon: Int)

                val access = listOf(
                    AccessItem(
                        title = "套餐臻选",
                        subTitle = "Package Recommendation",
                        icon = R.drawable.shampoo_set
                    ),
                    AccessItem(
                        title = "单品臻选",
                        subTitle = "Item Recommendation",
                        icon = R.drawable.shampoo
                    ),
                    AccessItem(
                        title = "历史档案",
                        subTitle = "Archives",
                        icon = R.drawable.history
                    ),
                )
                access.forEach { item ->
                    QuickAccess(
                        icon = item.icon,
                        title = item.title,
                        subTitle = item.subTitle
                    )
                }
            }

            Spacer(modifier = Modifier.height(38.dp))

            StepTitle(
                step = "01",
                title = "服务进行时",
                subTitle = "IN SESSION"
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .height(36.dp)
                    .padding(horizontal = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(1.dp)
            ) {
                // 未开始
                ServiceStatistices(count = 10, color = Color(0xFF6B8C84))
                // 进行中
                ServiceStatistices(count = 5, color = Color(0xFFF5A524))
                // 已完成
                ServiceStatistices(count = 0, color = Color(0xFF0FB48A))
            }

            Spacer(modifier = Modifier.height(15.dp))

            CreateService()

            Spacer(modifier = Modifier.height(20.dp))
            Box(
                modifier = Modifier
                    .height(1.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFDBEAE4))
                )
            }
            Spacer(modifier = Modifier.height(22.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 14.dp, end = 14.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ServiceCard()
                ServiceCard()
            }

            Copyright()
        }
    }

}


@Composable
fun QuickAccess(
    destination: String = "",
    icon: Int,
    title: String,
    subTitle: String
) {
    val hapticClick = rememberHapticClick()

    Button(
        onClick = {
            hapticClick()
        },
        shape = RectangleShape,
        contentPadding = PaddingValues(22.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFFFFFFF),
            contentColor = Color(0xFF36D2A6)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFFFFFFF)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            Row(
                modifier = Modifier
                    .size(46.dp)
                    .clip(shape = RoundedCornerShape(16.dp))
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF36D2A6),
                                Color(0xFF0FB48A)
                            )
                        )
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(icon),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .height(46.dp)
            ) {


                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0A1F1A),
                    fontFamily = FontFamily.Serif
                )
                Text(
                    text = subTitle,
                    fontSize = 14.sp,
                    fontStyle = FontStyle.Italic,
                    fontFamily = CormorantGaramondItalicFamily,
                    color = Color(0xFF7C948D),
                    letterSpacing = 2.sp
                )
            }

            Row(
                modifier = Modifier.size(34.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.arrow_right),
                    contentDescription = null,
                    tint = Color(0xFF0FB48A),
                    modifier = Modifier.size(21.dp)
                )
            }
        }
    }
}


@Composable
fun ServiceStatistices(
    count: Int,
    color: Color
) {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .background(Color(0xFFFFFFFF))
    ) {
        Row(
            modifier = Modifier
                .fillMaxHeight()
                .padding(horizontal = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(shape = RoundedCornerShape(8.dp))
                    .background(color)
            )
            Text(
                text = count.toString(),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = color,
                fontFamily = FontFamily.Serif
            )
        }
    }
}

@Composable
fun Copyright() {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 14.dp, end = 14.dp)
    ) {
        Spacer(modifier = Modifier.height(36.dp))

        Row(
            modifier = Modifier.height(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .height(1.dp)
                    .weight(1f)
                    .background(color = Color(0xFFDBEAE4))
            )
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .rotate(45f)
                    .background(color = Color(0xFF0FB48A))
            )
            Box(
                modifier = Modifier
                    .height(1.dp)
                    .weight(1f)
                    .background(color = Color(0xFFDBEAE4))
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "©",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = CormorantGaramondItalicFamily,
                color = Color(0xFF7C948D)
            )
            Text(
                modifier = Modifier.padding(start = 4.dp, end = 2.dp),
                text = "头皮养护助手 · ",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif,
                color = Color(0xFF7C948D)
            )
            Text(
                text = "Crafted with care",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = CormorantGaramondItalicFamily,
                color = Color(0xFF7C948D)
            )
        }

        Spacer(modifier = Modifier.height(60.dp))
    }
}

