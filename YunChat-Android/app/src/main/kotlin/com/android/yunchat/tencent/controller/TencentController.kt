package com.android.yunchat.tencent.controller

import android.app.Activity
import android.content.Context
import com.android.yunchat.state.GlobalState
import com.android.yunchat.tencent.model.TencentViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalPermissionsApi::class)
suspend fun handleTencentLogin(
    context: Context,
    tencentViewModel: TencentViewModel,
    deviceInfoPermissionState: PermissionState
) = withContext(Dispatchers.Main.immediate) {
    if (GlobalState.tencentListen.value) return@withContext

    if (!deviceInfoPermissionState.status.isGranted) {
        deviceInfoPermissionState.launchPermissionRequest()
        return@withContext
    }

    if (context is Activity) {
        tencentViewModel.handleLogin(context)
        GlobalState.tencentListen.value = true
    }
}