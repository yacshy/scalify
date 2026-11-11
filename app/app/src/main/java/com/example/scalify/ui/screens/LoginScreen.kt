package com.example.scalify.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.scalify.ui.components.LoginScreen.LoginPageBackground
import com.example.scalify.ui.components.LoginScreen.LoginPageBaseForm

@Composable
fun LoginPageUI(

) {
    LoginPageBackground(
        content = {
            LoginPageBaseForm()
        }
    )
}