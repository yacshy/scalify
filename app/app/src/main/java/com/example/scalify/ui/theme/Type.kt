package com.example.hair.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// 装饰性衬线（标题 / 品牌 / 章节号 / 统计数字）
val AppSerif = FontFamily.Serif
// 非装饰性正文（系统默认字体）
val AppSans = FontFamily.Default

/** 舒悦头皮护理专用字阶（对应 atelier.css --serif / --latin / --sans） */
object ScalpType {
    val Display = TextStyle(
        fontFamily = AppSerif, fontWeight = FontWeight.Bold,
        fontSize = 46.sp, lineHeight = 48.sp, color = Ink
    )
    val TitleSerif = TextStyle(
        fontFamily = AppSerif, fontWeight = FontWeight.Bold,
        fontSize = 24.sp, lineHeight = 28.sp, color = Ink
    )
    val CardName = TextStyle(
        fontFamily = AppSerif, fontWeight = FontWeight.Bold,
        fontSize = 19.sp, lineHeight = 24.sp, color = Ink
    )
    val StatNum = TextStyle(
        fontFamily = AppSerif, fontWeight = FontWeight.Bold,
        fontSize = 18.sp, lineHeight = 20.sp, color = Ink
    )
    val Body = TextStyle(
        fontFamily = AppSans, fontWeight = FontWeight.Normal,
        fontSize = 14.sp, lineHeight = 22.sp, color = InkSoft
    )
    val BodySmall = TextStyle(
        fontFamily = AppSans, fontWeight = FontWeight.Normal,
        fontSize = 13.sp, lineHeight = 20.sp, color = InkFaint
    )
    val Caption = TextStyle(
        fontFamily = AppSans, fontWeight = FontWeight.Normal,
        fontSize = 12.sp, lineHeight = 18.sp, color = InkFaint
    )
    val Label = TextStyle(
        fontFamily = AppSans, fontWeight = FontWeight.Medium,
        fontSize = 11.sp, lineHeight = 16.sp, color = InkFaint
    )
    // 拉丁点缀（装饰性斜体衬线，近似 Cormorant Garamond）
    val LatinItalic = TextStyle(
        fontFamily = AppSerif, fontWeight = FontWeight.Medium,
        fontStyle = FontStyle.Italic, fontSize = 15.sp, color = InkFaint
    )
}

val Typography = Typography(
    displaySmall = TextStyle(
        fontFamily = AppSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 25.sp,
        lineHeight = 30.sp
    ),
    titleMedium = TextStyle(
        fontFamily = AppSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 19.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = AppSans,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = AppSans,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    labelLarge = TextStyle(
        fontFamily = AppSans,
        fontWeight = FontWeight.Medium,
        fontSize = 13.sp,
        lineHeight = 18.sp
    ),
    labelSmall = TextStyle(
        fontFamily = AppSans,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp
    )
)
