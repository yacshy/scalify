package com.example.scalify.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class NavRoutes {
    @Serializable
    object LoginPage : NavRoutes()

    @Serializable
    object WorkbenchPage : NavRoutes()

    @Serializable
    object DetectPage : NavRoutes()

    @Serializable
    object ProfilePage : NavRoutes()

}