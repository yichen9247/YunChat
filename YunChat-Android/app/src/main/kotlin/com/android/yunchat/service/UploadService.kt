package com.android.yunchat.service

import android.net.Uri
import androidx.activity.result.ActivityResult
import com.android.yunchat.enmu.FileTypeEnum
import top.chengdongqing.weui.core.ui.components.toast.ToastState

interface UploadService {
    suspend fun handleUploadFile(
        uri: Uri,
        type: FileTypeEnum,
        toastState: ToastState,
        onSuccess: (path: String) -> Unit
    )

    suspend fun handleSelectFile(
        result: ActivityResult,
        toastState: ToastState,
        onSuccess: (path: String) -> Unit
    )

     suspend fun handleSelectImage(
        uri: Uri,
        toastState: ToastState,
        onSuccess: (path: String) -> Unit
    )

    suspend fun handleSelectAvatar(
        uri: Uri,
        toastState: ToastState,
        onSuccess: (path: String) -> Unit
    )

    suspend fun handleSelectVideo(
        uri: Uri,
        toastState: ToastState,
        onSuccess: (path: String) -> Unit
    )
}