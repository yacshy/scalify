package com.example.scalify.ui.components.LoginScreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scalify.R
import com.example.scalify.ui.components.common.GetVerifyCodeButton

@Composable
fun LoginPageRegisterForm(
    onClickLogin: (tab: CurrentTab) -> Unit
) {

    LoginTips(
        onClick = onClickLogin
    )
}

/**
 * 登录提示
 */
@Composable
fun LoginTips(
    onClick: (tab: CurrentTab) -> Unit
) {

    var phone by remember { mutableStateOf("") }
    var verifyCode by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rePassword by remember { mutableStateOf("") }

    var isShowPassword by remember { mutableStateOf(false) }
    var visualTransformation = if (isShowPassword) {
        VisualTransformation.None
    } else {
        PasswordVisualTransformation()
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        InputBox(
            value = password,
            onValueChange = { password = it },
            visualTransformation = visualTransformation,
            label = "手机号",
            placeholder = "请输入手机号",
            prefix = {
                Row(
                    modifier = Modifier
                        .size(42.dp),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.lock),
                        contentDescription = null,
                        tint = Color(0xFF7C948D),
                        modifier = Modifier
                            .fillMaxHeight()
                            .size(18.dp)
                    )
                }
            },
        )
        InputBox(
            value = password,
            onValueChange = { password = it },
            visualTransformation = visualTransformation,
            label = "短信验证码",
            placeholder = "请输入短信验证码",
            prefix = {
                Row(
                    modifier = Modifier
                        .size(42.dp),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.lock),
                        contentDescription = null,
                        tint = Color(0xFF7C948D),
                        modifier = Modifier
                            .fillMaxHeight()
                            .size(18.dp)
                    )
                }
            },
            surfix = {
                Row(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(top = 4.dp, bottom = 4.dp, start = 6.dp, end = 6.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    GetVerifyCodeButton()
                }
            }
        )
        InputBox(
            value = password,
            onValueChange = { password = it },
            visualTransformation = visualTransformation,
            label = "设置密码",
            placeholder = "请设置登录密码（至少6位）",
            prefix = {
                Row(
                    modifier = Modifier
                        .size(42.dp),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.lock),
                        contentDescription = null,
                        tint = Color(0xFF7C948D),
                        modifier = Modifier
                            .fillMaxHeight()
                            .size(18.dp)
                    )
                }
            },
            surfix = {
                Row(
                    modifier = Modifier
                        .size(42.dp),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.eye_off),
                        contentDescription = null,
                        tint = Color(0xFF7C948D),
                        modifier = Modifier
                            .fillMaxHeight()
                            .size(18.dp)
                    )
                }
            }
        )
        InputBox(
            value = password,
            onValueChange = { password = it },
            visualTransformation = visualTransformation,
            label = "确认密码",
            placeholder = "请再次输入密码",
            prefix = {
                Row(
                    modifier = Modifier
                        .size(42.dp),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.lock),
                        contentDescription = null,
                        tint = Color(0xFF7C948D),
                        modifier = Modifier
                            .fillMaxHeight()
                            .size(18.dp)
                    )
                }
            },
        )
        Spacer(modifier = Modifier.height(13.dp))
        SubmitButton()
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = "已有账号? ",
            fontSize = 12.sp,
            color = Color(0xFF7C948D),
            fontFamily = FontFamily.SansSerif,
        )
        Text(
            modifier = Modifier
                .clickable(
                    enabled = true,
                    onClick = {
                        onClick(CurrentTab.PASSWORD)
                    }
                ),
            text = "返回登录",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0A8C6A),
            fontFamily = FontFamily.SansSerif,
        )
    }
}