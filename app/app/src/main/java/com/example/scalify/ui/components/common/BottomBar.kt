package com.example.scalify.ui.components.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.scalify.R
import com.example.scalify.ui.navigation.LocalNavController
import com.example.scalify.ui.navigation.NavRoutes
import com.example.scalify.ui.uitls.rememberHapticClick

data class BottomBarNavItem(
    val icon: Int,
    val title: String,
    val destination: NavRoutes,
    val isChip: Boolean = false
)

@Composable
fun ButtomBarGuard() {
    val navController = LocalNavController.current
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    val currentDestination = navBackStackEntry?.destination
    val hideBottomBarRoutes = listOf(
        NavRoutes.LoginPage::class,
        NavRoutes.DetectPage::class,
        // 其他路由...
    )
    val shouldShowBottomBar = currentDestination?.let { destination ->
        hideBottomBarRoutes.none { routeClass ->
            destination.hasRoute(routeClass)
        }
    } ?: true

    if (shouldShowBottomBar) {
        BottomBar()
    }
}

@Composable
fun BottomBar() {
    val navController = LocalNavController.current

    Row(
        modifier = Modifier
            .height(65.dp)
            .fillMaxWidth()
            .background(Color(0xFFFFFFFF))
            .padding(vertical = 6.dp, horizontal = 8.dp),
    ) {
        val navItems = listOf(
            BottomBarNavItem(
                icon = R.drawable.workbench,
                title = "工作台",
                destination = NavRoutes.WorkbenchPage
            ),
            BottomBarNavItem(
                icon = R.drawable.camera,
                title = "检测",
                destination = NavRoutes.DetectPage,
                isChip = true
            ),
            BottomBarNavItem(
                icon = R.drawable.user,
                title = "我的",
                destination = NavRoutes.ProfilePage
            ),
        )

        var currentActiveIndex by remember { mutableStateOf(0) }

        navItems.forEachIndexed { index, item ->
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
            ) {
                BottomBarItem(item, navController)
            }
        }
    }
}

@Composable
fun BottomBarItem(
    navItem: BottomBarNavItem,
    navController: NavController
) {
    val hapticClick = rememberHapticClick()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    var actived =
        if (currentDestination?.hasRoute(navItem.destination::class) == true) true else false

    Button(
        modifier = Modifier.fillMaxSize(),
        onClick = {
            actived.let {
                if (!it) {
                    hapticClick()
                    navController.navigate(navItem.destination)
                }
            }
        },
        shape = RectangleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0x00FFFFFF)
        ),
        contentPadding = PaddingValues(0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            var color = if (actived) {
                Color(0xFF0FB48A)
            } else {
                Color(0xFF7C948D)
            }

            Row(
                modifier = Modifier
                    .height(4.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                AnimatedVisibility(
                    visible = actived,
                    enter = expandHorizontally(
                        animationSpec = tween(250),
                        expandFrom = Alignment.CenterHorizontally
                    ) + fadeIn(tween(250)),
                    exit = shrinkHorizontally(
                        animationSpec = tween(250),
                        shrinkTowards = Alignment.CenterHorizontally
                    ) + fadeOut(tween(250))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(38.dp)
                            .background(Color(0xFF0FB48A))
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(navItem.icon),
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = navItem.title,
                    fontSize = 11.sp,
                    lineHeight = 11.sp,
                    fontFamily = FontFamily.SansSerif,
                    color = color
                )
            }
        }
    }
}