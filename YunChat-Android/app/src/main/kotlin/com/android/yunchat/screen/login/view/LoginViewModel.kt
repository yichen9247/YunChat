package com.android.yunchat.screen.login.view

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.yunchat.enmu.LoginModeEnum
import com.android.yunchat.model.TencentLoginModel
import com.android.yunchat.model.UserAuthSendModel
import com.android.yunchat.service.instance.storageServiceInstance
import com.android.yunchat.service.instance.userServiceInstance
import com.xuexiang.xutil.XUtil
import kotlinx.coroutines.launch
import top.chengdongqing.weui.core.ui.components.toast.ToastState
import top.chengdongqing.weui.core.utils.showToast

/**
 * @name 登录ViewModel
 * @author yichen9247
 */
class LoginViewModel : ViewModel() {
    val username = mutableStateOf("")
    val password = mutableStateOf("")
    val passwordVisible = mutableStateOf(false)
    val currentLoginMod = mutableStateOf(LoginModeEnum.LOGIN)

    fun setUsernameValue(value: String) {
        username.value = value
    }

    fun setPasswordValue(value: String) {
        password.value = value
    }

    fun togglePasswordVisible() {
        passwordVisible.value = !passwordVisible.value
    }

    fun toggleCurrentLoginMode() {
        currentLoginMod.value = when(currentLoginMod.value) {
            LoginModeEnum.LOGIN -> LoginModeEnum.REGISTER
            LoginModeEnum.REGISTER -> LoginModeEnum.LOGIN
        }
        clearLoginInputs()
    }

    private fun clearLoginInputs() {
        username.value = ""
        password.value = ""
    }

    fun doUserLogin(
        context: Context,
        toastState: ToastState
    ) {
        if (!validateInputs()) return
        viewModelScope.launch {
            val authData = UserAuthSendModel(
                username = username.value.trim(),
                password = password.value
            )
            when (currentLoginMod.value) {
                LoginModeEnum.LOGIN ->
                    userServiceInstance.userLogin(
                        data = authData,
                        context = context,
                        toastState = toastState
                    )
                LoginModeEnum.REGISTER ->
                    userServiceInstance.userRegister(
                        data = authData,
                        context = context,
                        toastState = toastState
                    )
            }
        }
    }

    suspend fun doUserAutoLogin(
        context: Context,
        toastState: ToastState
    ) {
        storageServiceInstance.mmkv.decodeString("token") ?: return
        val method = storageServiceInstance.mmkv.decodeString("method") ?: return
        when (method) {
            "username" -> {
                getUsernameData()?.let { (user, pass) ->
                    username.value = user
                    password.value = pass
                    doUserLogin(context, toastState)
                }
            }
            "tencent" -> {
                getTencentData()?.let { (id, token) ->
                    userServiceInstance.userTencentLogin(
                        context = context,
                        toastState = toastState,
                        data = TencentLoginModel(openid = id, access_token = token)
                    )
                }
            }
        }
    }

    private fun validateInputs(): Boolean {
        return when {
            username.value.isEmpty() || password.value.isEmpty() -> {
                XUtil.getContext().showToast("账号或密码不能为空")
                false
            }
            else -> true
        }
    }

    private fun getUsernameData(): UserAuthSendModel? {
        val usernameCache = storageServiceInstance.mmkv.decodeString("username")
        val passwordCache = storageServiceInstance.mmkv.decodeString("password")
        return if (!usernameCache.isNullOrEmpty() && !passwordCache.isNullOrEmpty()) {
            UserAuthSendModel(
                username = usernameCache,
                password = passwordCache
            )
        } else null
    }

    private fun getTencentData(): TencentLoginModel? {
        val openid = storageServiceInstance.mmkv.decodeString("openid")
        val access_token = storageServiceInstance.mmkv.decodeString("access_token")
        return if (!openid.isNullOrEmpty() && !access_token.isNullOrEmpty()) {
            TencentLoginModel(
                openid = openid,
                access_token = access_token
            )
        } else null
    }
}