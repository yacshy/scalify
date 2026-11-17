package com.example.scalify.ui.components.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scalify.ui.theme.CormorantGaramondItalicFamily

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
                    .height(1.dp)
                    .weight(1f)
                    .background(color = Color(0xFFDBEAE4))
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(
                space = 4.dp,
                alignment = Alignment.CenterHorizontally
            ),
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
                text = "头皮养护助手",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif,
                color = Color(0xFF7C948D)
            )
            Box(
                modifier = Modifier
                    .size(4.dp)
                    .background(color = Color(0xFF0FB48A), shape = CircleShape)
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