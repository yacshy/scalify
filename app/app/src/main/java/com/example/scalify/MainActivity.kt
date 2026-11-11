package com.example.scalify

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.rememberNavController
import com.example.scalify.ui.components.common.ButtomBarGuard
import com.example.scalify.ui.navigation.LocalNavController
import com.example.scalify.ui.navigation.NavGraph
import com.example.scalify.ui.theme.ScalifyTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ScalifyTheme() {
                val navController = rememberNavController()


                CompositionLocalProvider(
                    LocalNavController provides navController
                ) {
                    Scaffold(
                        modifier = Modifier.background(Color(0xFF0A6E58)),
                        bottomBar = {
                            ButtomBarGuard()
                        }
                    ) { outerPadding ->
                        Column(modifier = Modifier.padding(outerPadding)) {
                            NavGraph()
                        }
                    }
                }
            }
        }
    }
}

