package com.android.yunchat.request.model

import android.app.Activity
import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.yunchat.activity.MainActivity
import com.android.yunchat.model.ResultModel
import com.android.yunchat.model.TencentLoginModel
import com.android.yunchat.model.UserAuthModel
import com.android.yunchat.model.UserAuthSendModel
import com.android.yunchat.model.UserBindModel
import com.android.yunchat.model.UserInfoModel
import com.android.yunchat.model.UserInfoUpdateModel
import com.android.yunchat.model.UserScanLoginModel
import com.android.yunchat.request.repository.impl.UserRequestRepositoryImpl
import com.android.yunchat.service.instance.activityServiceInstance
import com.android.yunchat.service.instance.requestServiceInstance
import com.android.yunchat.service.instance.routeServiceInStance
import com.android.yunchat.service.instance.storageServiceInstance
import com.android.yunchat.state.GlobalState
import com.xuexiang.xutil.XUtil
import kotlinx.coroutines.launch
import top.chengdongqing.weui.core.ui.components.toast.ToastIcon
import top.chengdongqing.weui.core.ui.components.toast.ToastState
import top.chengdongqing.weui.core.utils.showToast
import kotlin.time.Duration

class UserRequestViewModel : ViewModel() {
    private val userServiceRepository by lazy {
        UserRequestRepositoryImpl()
    }
    private val loginIng = mutableStateOf(false)

    fun showLoadingToast(
        toastState: ToastState,
        message: String = "正在请求中"
    ) {
        toastState.show(
            title = message,
            icon = ToastIcon.LOADING,
            duration = Duration.INFINITE
        )
    }

    fun fetchUserLogin(
        context: Context,
        toastState: ToastState,
        data: UserAuthSendModel
    ) {
        executeAuthRequest(
            context = context,
            toastState = toastState,
            request = { userServiceRepository.fetchUserLogin(data) },
            storageHandler = { mmkv, responseData ->
                storageServiceInstance.mmkv.apply {
                    encode("method", "username")
                    encode("username", data.username)
                    encode("password", data.password)
                    encode("uid", responseData.userinfo.uid)
                }
            }
        )
    }

    fun fetchUserRegister(
        context: Context,
        toastState: ToastState,
        data: UserAuthSendModel
    ) {
        executeAuthRequest(
            context = context,
            toastState = toastState,
            request = { userServiceRepository.fetchUserRegister(data) },
            storageHandler = { mmkv, responseData ->
                storageServiceInstance.mmkv.apply {
                    encode("method", "username")
                    encode("username", data.username)
                    encode("password", data.password)
                    encode("uid", responseData.userinfo.uid)
                }
            }
        )
    }

    fun fetchUserQQLogin(
        context: Context,
        toastState: ToastState,
        data: TencentLoginModel
    ) {
        executeAuthRequest(
            context = context,
            toastState = toastState,
            request = { userServiceRepository.fetchUserQQLogin(data) },
            storageHandler = { mmkv, responseData ->
                storageServiceInstance.mmkv.apply {
                    encode("method", "tencent")
                    encode("openid", data.openid)
                    encode("uid", responseData.userinfo.uid)
                    encode("access_token", data.access_token)
                }
            }
        )
    }

    private fun executeAuthRequest(
        context: Context,
        toastState: ToastState,
        message: String = "正在登录中",
        request: suspend () -> ResultModel<UserAuthModel>?,
        storageHandler: (mmkv: Any, responseData: UserAuthModel) -> Unit
    ) {
        if (loginIng.value) return
        showLoadingToast(
            message = message,
            toastState = toastState
        )
        viewModelScope.launch {
            try {
                loginIng.value = true
                val response = request()
                response?.let {
                    requestServiceInstance.checkResponseResult(
                        response = it,
                        message = "操作失败",
                        onSuccess = {
                            it.data?.let { authData ->
                                handleAuthSuccess(context, authData, storageHandler)
                            }
                        }
                    )
                }
            } catch (e: Exception) {
                context.showToast(e.message ?: "操作失败")
            } finally {
                toastState.hide()
                loginIng.value = false
            }
        }
    }

    private fun handleAuthSuccess(
        context: Context,
        data: UserAuthModel,
        storageHandler: (mmkv: Any, responseData: UserAuthModel) -> Unit
    ) {
        GlobalState.userInfo.value = data.userinfo
        storageServiceInstance.mmkv.let { mmkv ->
            mmkv.encode("token", data.token)
            storageHandler(mmkv, data)
        }
        navigateToMainActivity(context)
    }

    private fun navigateToMainActivity(context: Context) {
        viewModelScope.launch {
            kotlinx.coroutines.delay(200)
            activityServiceInstance.intentActivityAboutContext(context, MainActivity::class.java)
            (context as Activity).finish()
        }
    }

    fun fetchUserScanLogin(qid: String, status: Int) {
        viewModelScope.launch {
            userServiceRepository.fetchUserScanLogin(
                UserScanLoginModel(qid = qid, status = status)
            )?.let { response ->
                requestServiceInstance.checkResponseResult(
                    message = "登录失败",
                    response = response,
                    onSuccess = {
                        if (status != 0) {
                            XUtil.getContext().showToast("操作成功")
                        }
                    }
                )
            }
            routeServiceInStance.navigationBack()
        }
    }

    suspend fun fetchUserScanStatus(qid: String): Boolean {
        return userServiceRepository.fetchUserScanStatus(
            UserScanLoginModel(qid = qid, status = 0)
        )?.let { response ->
            when (response.code) {
                400 -> routeServiceInStance.navigationTo("authorize", false)
                200 -> XUtil.getContext().showToast("二维码已使用")
                else -> XUtil.getContext().showToast("二维码已过期")
            }
            true
        } ?: false
    }

    suspend fun fetchUserInfo(uid: Long): ResultModel<UserInfoModel>? {
        return userServiceRepository.fetchUserInfo(uid)
    }

    suspend fun fetchUpdateUserNick(data: String): ResultModel<*>? {
        return userServiceRepository.fetchUpdateUserNick(UserInfoUpdateModel(
            data = data
        ))
    }

    suspend fun fetchUpdateUserAvatar(data: String): ResultModel<*>? {
        return userServiceRepository.fetchUpdateUserAvatar(UserInfoUpdateModel(
            data = data
        ))
    }

    suspend fun fetchUpdatePassword(data: String): ResultModel<*>? {
        return userServiceRepository.fetchUpdatePassword(UserInfoUpdateModel(
            data = data
        ))
    }

    suspend fun fetchUserBind(): ResultModel<UserBindModel>? {
        return userServiceRepository.fetchUserBind()
    }

    suspend fun fetchUserQQBind(data: TencentLoginModel, status: Int): ResultModel<*>? {
        return userServiceRepository.fetchUserQQBind(data, status)
    }
}