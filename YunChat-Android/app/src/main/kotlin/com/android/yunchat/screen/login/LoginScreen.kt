package com.android.yunchat.screen.login

import android.Manifest
import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.yunchat.R
import com.android.yunchat.enmu.LoginModeEnum
import com.android.yunchat.screen.login.view.LoginViewModel
import com.android.yunchat.service.instance.dialogServiceInstance
import com.android.yunchat.service.instance.userServiceInstance
import com.android.yunchat.tencent.TencentCallback
import com.android.yunchat.tencent.controller.handleTencentLogin
import com.android.yunchat.tencent.model.TencentViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.launch
import top.chengdongqing.weui.core.ui.components.dialog.rememberDialogState
import top.chengdongqing.weui.core.ui.components.toast.rememberToastState
import top.chengdongqing.weui.core.utils.showToast

@Composable
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
fun LoginScreen(
    loginViewModel: LoginViewModel = viewModel()
) {
    val toastState = rememberToastState()
    val dialogState = rememberDialogState()

    val context = LocalContext.current
    val username = loginViewModel.username.value
    val password = loginViewModel.password.value
    val coroutineScope = rememberCoroutineScope()
    val passwordVisible = loginViewModel.passwordVisible.value
    val currentLoginMod = loginViewModel.currentLoginMod.value
    val keyboardController = LocalSoftwareKeyboardController.current

    fun doUserLogin() {
        keyboardController?.hide()
        loginViewModel.doUserLogin(
            context = context,
            toastState = toastState
        )
    }

    LaunchedEffect(Unit) {
        loginViewModel.doUserAutoLogin(
            context = context,
            toastState = toastState
        )
    }

    TencentCallback({
        coroutineScope.launch {
            userServiceInstance.userTencentLogin(
                data = it,
                context = context,
                toastState = toastState
            )
        }
    })

    Scaffold(
        bottomBar = {
            FastLogin()
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.onBackground),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(40.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    text = "${
                        when(currentLoginMod) {
                            LoginModeEnum.LOGIN -> "登录"
                            LoginModeEnum.REGISTER -> "注册"
                    }}到${context.getString(R.string.app_name)}",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.headlineMedium,
                )
                Spacer(modifier = Modifier.height(28.dp))
                OutlinedTextField(
                    value = username,
                    shape = RoundedCornerShape(6.dp),
                    onValueChange = {
                        loginViewModel.setUsernameValue(it)
                    },
                    label = { Text("账号") },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "账号图标"
                        )
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Next,
                        keyboardType = KeyboardType.Text
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(20.dp))
                OutlinedTextField(
                    value = password,
                    shape = RoundedCornerShape(6.dp),
                    onValueChange = {
                        loginViewModel.setPasswordValue(it)
                    },
                    label = { Text("密码") },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Lock,
                            contentDescription = "密码图标"
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = loginViewModel::togglePasswordVisible) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                contentDescription = if (passwordVisible) "隐藏密码" else "显示密码"
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible)
                        VisualTransformation.None
                    else
                        PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done,
                        keyboardType = KeyboardType.Password
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(28.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        fontSize = 16.sp,
                        text = when(currentLoginMod) {
                            LoginModeEnum.LOGIN -> "立即注册"
                            LoginModeEnum.REGISTER -> "返回登录"
                        },
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .clickable(
                                indication = null,
                                interactionSource = null,
                                onClick = {
                                    loginViewModel.toggleCurrentLoginMode()
                                }
                            )
                    )

                    if (currentLoginMod == LoginModeEnum.LOGIN) Text(
                        fontSize = 16.sp,
                        text = "忘记密码",
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .clickable(
                                indication = null,
                                interactionSource = null,
                                onClick = {
                                    dialogServiceInstance.openForgetPasswordDialog(dialogState)
                                }
                            )
                    )
                }
                Spacer(modifier = Modifier.height(28.dp))
                Button(
                    onClick = {
                        doUserLogin()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "立即${
                            when(currentLoginMod) {
                                LoginModeEnum.LOGIN -> "登录"
                                LoginModeEnum.REGISTER -> "注册"
                            }}",
                        fontSize = 16.sp,
                        color = Color.White,
                    )
                }
            }
        }
    }
}

@Composable
private fun FastLogin() {
    /*TencentListener()*/
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(40.dp)
            .absoluteOffset(0.dp, 0.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(0.5.dp)
                        .background(Color(0XFFE3E8F7))
                )
                Spacer(modifier = Modifier.width(15.dp))
                Text(
                    fontSize = 12.sp,
                    text = "其它登录方式",
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.width(15.dp))
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(0.5.dp)
                        .background(Color(0XFFE3E8F7))
                )
            }
            Spacer(modifier = Modifier.height(25.dp))
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LoginMethodBox(
                    key = "qq",
                    painter = R.drawable.ic_login_qq
                )
                Spacer(modifier = Modifier.width(25.dp))
                LoginMethodBox(
                    key = "wechat",
                    painter = R.drawable.ic_login_wechat
                )
                Spacer(modifier = Modifier.width(25.dp))
                LoginMethodBox(
                    key = "weibo",
                    painter = R.drawable.ic_login_weibo
                )
                Spacer(modifier = Modifier.width(25.dp))
                LoginMethodBox(
                    key = "alipay",
                    painter = R.drawable.ic_login_alipay
                )
                Spacer(modifier = Modifier.width(25.dp))
                LoginMethodBox(
                    key = "netease",
                    painter = R.drawable.ic_login_netease
                )
            }
        }
    }
}

@Composable
@OptIn(ExperimentalPermissionsApi::class, DelicateCoroutinesApi::class)
private fun LoginMethodBox(
    key: String,
    painter: Int,
    tencentViewModel: TencentViewModel = viewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val deviceInfoPermissionState = rememberPermissionState(
        Manifest.permission.READ_PHONE_STATE
    )

    Image(
        modifier = Modifier
            .size(35.dp)
            .clickable(
                onClick = {
                    scope.launch {
                        when(key) {
                            "qq" -> handleTencentLogin(
                                context = context,
                                tencentViewModel = tencentViewModel,
                                deviceInfoPermissionState = deviceInfoPermissionState
                            )
                            else -> context.showToast("暂未支持该登录方式")
                        }
                    }
                },
                indication = null,
                interactionSource = null
            ),
        painter = painterResource(painter),
        contentDescription = "Login Method"
    )
}