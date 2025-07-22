package com.android.yunchat.service

import android.content.Context
import com.android.yunchat.model.UpdateInfoModel
import top.chengdongqing.weui.core.ui.components.dialog.DialogState
import top.chengdongqing.weui.core.ui.components.toast.ToastState

interface DialogService {
    fun openExitLoginDialog(context: Context)
    fun openSearchGroupDialog(context: Context)
    fun openClearAppCacheDialog(context: Context)
    fun openBatteryOptimizationDialog(context: Context)
    fun openForgetPasswordDialog(dialogState: DialogState)
    fun openEditPasswordDialog(context: Context, callback: (String) -> Unit)
    fun openExitGroupDialog(
        gid: Int,
        name: String,
        context: Context
    )
    fun openEditUserNickDialog(
        nick: String,
        context: Context,
        callback: (String) -> Unit
    )
    fun openUpdateApplicationDialog(context: Context, data: UpdateInfoModel)
    fun openSaveImageDialog(
        url: String,
        toastState: ToastState,
        dialogState: DialogState
    )
}