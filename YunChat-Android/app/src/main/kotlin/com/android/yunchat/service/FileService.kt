package com.android.yunchat.service

import android.content.Context
import top.chengdongqing.weui.core.data.model.MediaType
import top.chengdongqing.weui.core.ui.components.toast.ToastState

interface FileService {
    suspend fun saveImage(
        content: String,
        toastState: ToastState
    )

    suspend fun openSystemFilePreview(
        content: String,
        toastState: ToastState
    )

    suspend fun openMediaPreview(
        content: String,
        context: Context,
        mediaType: MediaType,
        toastState: ToastState
    )
}