package com.android.yunchat.screen.user.view

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.yunchat.model.ResultModel
import com.android.yunchat.model.UserInfoModel
import com.android.yunchat.request.model.UserRequestViewModel
import com.android.yunchat.service.instance.requestServiceInstance
import com.android.yunchat.service.instance.uploadServiceInstance
import com.android.yunchat.service.instance.userServiceInstance
import com.android.yunchat.state.GlobalState
import com.xuexiang.xutil.XUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import top.chengdongqing.weui.core.data.model.MediaItem
import top.chengdongqing.weui.core.ui.components.toast.ToastState
import top.chengdongqing.weui.core.utils.showToast

/**
 * @name 用户ViewModel
 * @author yichen9247
 */
class UserViewModel : ViewModel() {
    val userInfo = mutableStateOf<UserInfoModel?>(null)

    fun getUserInfo(uid: Long) {
        if (uid == userServiceInstance.getUserUid()) {
            userInfo.value = GlobalState.userInfo.value
            return
        } // 减少请求次数
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                UserRequestViewModel().fetchUserInfo(uid)
            }
            result?.let { response ->
                requestServiceInstance.checkResponseResult(
                    response = response,
                    message = "获取用户数据失败",
                    onSuccess = {
                        userInfo.value = response.data
                    }
                )
            }
        }
    }

    fun updateUserNick(data: String) = updateField(
        data = data,
        emptyErrorMessage = "昵称不能为空",
        request = { UserRequestViewModel().fetchUpdateUserNick(data) },
        errorMessage = "修改昵称失败",
        onSuccess = { response ->
            val newInfo = userInfo.value?.copy(nick = data)
            userInfo.value = newInfo
            GlobalState.userInfo.value = newInfo
            response?.message ?: "修改昵称成功"
        }
    )

    fun updatePassword(data: String) = updateField(
        data = data,
        emptyErrorMessage = "密码不能为空",
        request = { UserRequestViewModel().fetchUpdatePassword(data) },
        errorMessage = "修改密码失败",
        onSuccess = { response -> response?.message ?: "修改密码成功" }
    )

    private fun updateUserAvatar(data: String) = updateField(
        data = data,
        emptyErrorMessage = "头像不能为空",
        request = { UserRequestViewModel().fetchUpdateUserAvatar(data) },
        errorMessage = "修改头像失败",
        onSuccess = { response ->
            val newInfo = userInfo.value?.copy(avatar = data)
            userInfo.value = newInfo
            GlobalState.userInfo.value = newInfo
            response?.message ?: "修改头像成功"
        }
    )

    private inline fun updateField(
        data: String,
        emptyErrorMessage: String,
        crossinline request: suspend () -> ResultModel<*>??,
        errorMessage: String,
        crossinline onSuccess: (ResultModel<*>?) -> String
    ) {
        if (data.isEmpty()) {
            XUtil.getContext().showToast(emptyErrorMessage)
            return
        }
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) { request() }
            handleResponse(
                response = result,
                errorMessage = errorMessage,
                onSuccess = { response ->
                    XUtil.getContext().showToast(onSuccess(response))
                }
            )
        }
    }

    fun handleSelectAvatar(
        list: Array<MediaItem>,
        toastState: ToastState
    ) {
        viewModelScope.launch {
            uploadServiceInstance.handleSelectAvatar(list.first().uri, toastState) {
                updateUserAvatar(it)
            }
        }
    }

    private inline fun handleResponse(
        errorMessage: String,
        response: ResultModel<*>?,
        crossinline onSuccess: (ResultModel<*>?) -> Unit
    ) {
        response?.let {
            requestServiceInstance.checkResponseResult(
                response = it,
                message = errorMessage,
                onSuccess = { onSuccess(it) }
            )
        }
    }
}