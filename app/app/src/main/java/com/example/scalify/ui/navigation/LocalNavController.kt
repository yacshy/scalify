package com.example.scalify.ui.navigation

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavHostController

// 1. 定义 CompositionLocal
val LocalNavController = staticCompositionLocalOf<NavHostController> {
    error("NavController not provided")
}