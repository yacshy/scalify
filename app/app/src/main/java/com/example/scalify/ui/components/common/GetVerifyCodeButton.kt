package com.example.scalify.ui.components.common

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GetVerifyCodeButton() {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .clip(shape = RectangleShape)
            .border(
                width = 1.dp,
                color = Color(0xFF0FB48A)
            ),
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = {},
            contentPadding = PaddingValues(0.dp),
            shape = RectangleShape,
            modifier = Modifier
                .fillMaxHeight(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0x00000000),
                contentColor = Color(0xFF0FB48A)
            ),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "获取验证码",
                    color = Color(0xFF0FB48A),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.W600,
                    fontFamily = FontFamily.SansSerif
                )
            }
        }
    }
}