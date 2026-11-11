package com.example.scalify.ui.components.WorkbenchScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Advertisement() {
    val screenWidth = LocalConfiguration.current.screenWidthDp
    // 广告宽高比位 1 : 1
    val advertisimentHeight = screenWidth

    var advertisements = listOf(
        Color(0xFFF5A524),
        Color(0xFF05A524),
        Color(0xFFFF0500),
        Color(0xFF00F0F0)
    )

    val carouselState = rememberCarouselState { advertisements.size }

    HorizontalMultiBrowseCarousel(
        modifier = Modifier
            .fillMaxWidth()
            .height(advertisimentHeight.dp),
        state = carouselState,
        preferredItemWidth = screenWidth.dp,
        itemSpacing = 0.dp,
        contentPadding = PaddingValues(horizontal = 0.dp),
        minSmallItemWidth = 0.dp
    ) { index ->
        val item = advertisements[index]
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(item)
                .zIndex(1f)
        )
    }
}