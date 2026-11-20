package com.example.scalify.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.scalify.ui.screens.DetectPageUI
import com.example.scalify.ui.screens.LoginPageUI
import com.example.scalify.ui.screens.MajorCoursesPageUI
import com.example.scalify.ui.screens.ProfilePageUI
import com.example.scalify.ui.screens.WorkbenchPageUI

@Composable
fun NavGraph() {
    val navController = LocalNavController.current
    NavHost(
        navController = navController,
        startDestination = NavRoutes.ProfilePage
    ) {
        composable<NavRoutes.LoginPage> { LoginPageUI() }
        composable<NavRoutes.WorkbenchPage> { WorkbenchPageUI() }
        composable<NavRoutes.DetectPage> { DetectPageUI() }
        composable<NavRoutes.ProfilePage> { ProfilePageUI() }
        composable<NavRoutes.MajorCoursesPage> { MajorCoursesPageUI() }
    }
}