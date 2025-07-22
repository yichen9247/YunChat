package com.android.yunchat.service

import android.content.Context
import com.android.yunchat.model.TencentLoginModel
import com.android.yunchat.model.UserAuthSendModel
import top.chengdongqing.weui.core.ui.components.toast.ToastState

interface UserService {
    fun getUserUid(): Long
    fun getUserToken(): String

    suspend fun userLogin(
        context: Context,
        toastState: ToastState,
        data: UserAuthSendModel
    )

    suspend fun userRegister(
        context: Context,
        toastState: ToastState,
        data: UserAuthSendModel
    )

    suspend fun userTencentLogin(
        context: Context,
        toastState: ToastState,
        data: TencentLoginModel
    )
}