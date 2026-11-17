package com.example.scalify.ui.components.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.scalify.ui.theme.CormorantGaramondItalicFamily
import com.example.scalify.ui.uitls.rememberHapticClick

@Composable
fun QuickAccess(
    destination: String = "",
    icon: Int,
    title: String,
    subTitle: String,
    hoverColor: Color = Color(0xFF36D2A6),
    themeColor: Color = Color(0xFF0FB48A)
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
            contentColor = hoverColor
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
                                hoverColor,
                                themeColor
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
                    modifier = Modifier.size(21.dp),
                    painter = painterResource(R.drawable.arrow_right),
                    contentDescription = null,
                    tint = themeColor,
                )
            }
        }
    }
}