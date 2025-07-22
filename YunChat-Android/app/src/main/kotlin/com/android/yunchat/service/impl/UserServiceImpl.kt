package com.android.yunchat.service.impl

import android.content.Context
import com.android.yunchat.model.TencentLoginModel
import com.android.yunchat.model.UserAuthSendModel
import com.android.yunchat.request.model.UserRequestViewModel
import com.android.yunchat.service.UserService
import com.android.yunchat.service.instance.storageServiceInstance
import top.chengdongqing.weui.core.ui.components.toast.ToastState

class UserServiceImpl : UserService {
    override suspend fun userLogin(
        context: Context,
        toastState: ToastState,
        data: UserAuthSendModel
    ) = performAuthOperation(
        data = data,
        operation = { model -> userRequestViewModel.fetchUserLogin(
            data = model,
            context = context,
            toastState = toastState
        ) }
    )

    override suspend fun userRegister(
        context: Context,
        toastState: ToastState,
        data: UserAuthSendModel
    ) = performAuthOperation(
        data = data,
        operation = { model -> userRequestViewModel.fetchUserRegister(
            data = model,
            context = context,
            toastState = toastState
        ) }
    )

    override suspend fun userTencentLogin(
        context: Context,
        toastState: ToastState,
        data: TencentLoginModel
    ) = performAuthOperation(
        data = data,
        operation = { model -> userRequestViewModel.fetchUserQQLogin(
            data = model,
            context = context,
            toastState = toastState
        ) }
    )

    /**
     * @name 通用认证操作
     * @param T 认证数据类型
     * @param operation 具体执行的操作
     */
    private suspend fun <T> performAuthOperation(
        data: T,
        operation: suspend (T) -> Unit
    ) {
        operation(data)
    }

    override fun getUserUid(): Long = mmkv.decodeLong("uid", 0)
    override fun getUserToken(): String = mmkv.decodeString("token", "").orEmpty()

    companion object {
        private val mmkv by lazy { storageServiceInstance.mmkv }
        private val userRequestViewModel by lazy {
            UserRequestViewModel()
        }
    }
}