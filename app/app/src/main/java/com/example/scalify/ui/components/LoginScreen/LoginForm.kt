package com.example.scalify.ui.components.LoginScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scalify.R
import com.example.scalify.ui.components.common.GetVerifyCodeButton

@Composable
fun LoginPageLoginForm(
    currentTab: CurrentTab,
    onClickRegister: (tab: CurrentTab) -> Unit
) {
    var account by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var verifyCode by remember { mutableStateOf("") }

    InputBox(
        value = account,
        onValueChange = { account = it },
        label = "账号/手机号",
        placeholder = "请输入账号或手机号",
        prefix = {
            Row(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(42.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.user),
                    contentDescription = null,
                    tint = Color(0xFF7C948D),
                    modifier = Modifier
                        .fillMaxHeight()
                        .size(18.dp)
                )
            }
        },
    )
    if (currentTab == CurrentTab.PASSWORD) {
        var isShowPassword by remember { mutableStateOf(false) }
        var visualTransformation = if (isShowPassword) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        }
        InputBox(
            value = password,
            onValueChange = { password = it },
            visualTransformation = visualTransformation,
            label = "密码",
            placeholder = "请输入密码",
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
                        .size(42.dp)
                        .clickable(onClick = {
                            isShowPassword = !isShowPassword
                        }),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = painterResource(if (isShowPassword) R.drawable.eye_open else R.drawable.eye_off),
                        contentDescription = null,
                        tint = Color(0xFF7C948D),
                        modifier = Modifier
                            .fillMaxHeight()
                            .size(18.dp)
                    )
                }
            }
        )
    } else if (currentTab == CurrentTab.VERIFYCODE) {
        InputBox(
            value = verifyCode,
            onValueChange = { verifyCode = it },
            label = "短信验证码",
            placeholder = "请输入密码",
            prefix = {
                Row(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(42.dp),
                    horizontalArrangement = Arrangement.Center
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
    }

    LoginSettingOption()
    SubmitButton()
    RegisterTips(onClick = onClickRegister)
}

/** 输入框 */
@Composable
fun InputBox(
    value: String,
    onValueChange: (v: String) -> Unit,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    label: String,
    placeholder: String,
    prefix: @Composable () -> Unit = {},
    surfix: @Composable () -> Unit = {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(12.dp)
        )
    },
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            color = Color(0xFF3F5B54),
            fontWeight = FontWeight.Medium,
            fontFamily = FontFamily.SansSerif,
            modifier = Modifier.padding(top = 10.dp, bottom = 4.dp)
        )
        Row(
            modifier = Modifier
                .height(42.dp)
                .fillMaxWidth()
                .border(
                    width = 1.dp, color = Color(0xFFC2D9D1)
                ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // 左图标
            prefix()

            // 输入框
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 10.dp, bottom = 10.dp)
                    .fillMaxHeight(),
                contentAlignment = Alignment.CenterStart
            ) {
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        fontSize = 14.sp,
                        lineHeight = 14.sp,
                        color = Color(0xFFA8BDB7),
                        fontFamily = FontFamily.SansSerif,
                    )
                }
                BasicTextField(
                    readOnly = false,
                    enabled = true,
                    value = value,
                    textStyle = TextStyle(
                        fontSize = 14.sp,
                        lineHeight = 14.sp,
                        color = Color(0xFF0A1F1A),
                        fontFamily = FontFamily.SansSerif,
                    ),
                    onValueChange = onValueChange,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = visualTransformation
                )
            }

            // 右图标
            surfix()
        }
    }
}

/**
 * 记住我、用验证码找回密码
 */
@Composable
fun LoginSettingOption() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp, bottom = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            var checkState by remember { mutableStateOf(false) }
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .border(width = 1.dp, color = Color(0xFF0FB48A), shape = RectangleShape)
                    .background(
                        color = Color(0x00000000),
                        shape = RectangleShape
                    )
                    .clip(shape = RectangleShape)
            ) {
                Checkbox(
                    checked = checkState,
                    onCheckedChange = { checkState = it },
                    colors = CheckboxDefaults.colors(
                        uncheckedColor = Color(0x00000000),
                        checkedColor = Color(0xFF0FB48A),
                        checkmarkColor = Color(0xFFFFFFFF),
                    ),
                    modifier = Modifier
                        .fillMaxSize()
                )
            }
            Text(
                text = "记住我",
                fontSize = 13.sp,
                color = Color(0xFF3F5B54),
                fontFamily = FontFamily.SansSerif,
            )
        }
        Text(
            text = "验证码找回密码",
            fontSize = 13.sp,
            color = Color(0xFF0A8C6A),
            fontFamily = FontFamily.SansSerif,
        )
    }
}

/**
 * 提交按钮
 */
@Composable
fun SubmitButton() {
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .height(42.dp)
            .shadow(
                elevation = 20.dp,
                shape = RectangleShape,
                clip = false,
                ambientColor = Color(0xFF0FB48A),
                spotColor = Color(0xFF0FB48A)
            ),
        onClick = {},
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF0FB48A)
        ),
        shape = RectangleShape
    ) {
        Text(
            text = "登 录",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFFFFFFF),
            fontFamily = FontFamily.SansSerif,
        )
    }
}

/**
 * 注册提示
 */
@Composable
fun RegisterTips(
    onClick: (tab: CurrentTab) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = "还没有账号? ",
            fontSize = 12.sp,
            color = Color(0xFF7C948D),
            fontFamily = FontFamily.SansSerif,
        )
        Text(
            modifier = Modifier
                .clickable(
                    onClick = {
                        onClick(CurrentTab.REGISTER)
                    }
                ),
            text = "立即注册",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0A8C6A),
            fontFamily = FontFamily.SansSerif,
        )
    }
}