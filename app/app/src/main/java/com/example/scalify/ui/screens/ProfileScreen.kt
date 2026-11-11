package com.example.scalify.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp

@Composable
fun ProfilePageUI(
) {
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE8F4F0))
            .verticalScroll(scrollState)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            UserHero()
        }
    }
}

@Composable
fun UserHero() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(600.dp)
            .drawBehind {
                // 【底层】线性渐变 base
                drawLinearGradientBase()
                // 往上叠加3层径向渐变（顺序和CSS一致，后绘制在下层）
                drawRadial3()
                drawRadial2()
                drawRadial1()
            }
    ) {

    }
}

// 底层 linear-gradient(135deg, #0A8C6A 0%, #0FB48A 48%, #36D2A6 100%)
fun DrawScope.drawLinearGradientBase() {
    val brush = Brush.linearGradient(
        colors = listOf(
            Color(0xFF0A8C6A),
            Color(0xFF0FB48A),
            Color(0xFF36D2A6),
        ),
        start = Offset(1f, 0f),
        end = Offset(0f, 1f),
    )
    drawRect(brush = brush)
}

// 第3层 radial-gradient(60% 78% at 80% 86%, rgba(54,210,166,.55), transparent 62%)
private fun DrawScope.drawRadial3() {
    val center = Offset(size.width * 0.80f, size.height * 0.86f)
    val brush = Brush.radialGradient(
        colors = listOf(
            Color(0x8C36D2A6), // rgba(54,210,166,.55)
            Color.Transparent
        ),
        center = center,
        radius = size.maxDimension * 0.78f,
    )
    drawRect(brush = brush)
}

// 第2层 radial-gradient(95% 84% at 10% 108%, rgba(0,0,0,.20), transparent 60%)
private fun DrawScope.drawRadial2() {
    val center = Offset(size.width * 0.10f, size.height * 1.08f)
    val brush = Brush.radialGradient(
        colors = listOf(
            Color(0x33000000), // rgba(0,0,0,.20)
            Color.Transparent
        ),
        center = center,
        radius = size.maxDimension * 0.84f,
    )
    drawRect(brush = brush)
}

// 第1层（最上层）radial-gradient(120% 92% at 86% 12%, rgba(255,255,255,.30), transparent 56%)
private fun DrawScope.drawRadial1() {
    val center = Offset(size.width * 0.86f, size.height * 0.12f)
    val brush = Brush.radialGradient(
        colors = listOf(
            Color(0x4DFFFFFF), // rgba(255,255,255,.30)
            Color.Transparent
        ),
        center = center,
        radius = size.maxDimension * 0.92f,
    )
    drawRect(brush = brush)
}