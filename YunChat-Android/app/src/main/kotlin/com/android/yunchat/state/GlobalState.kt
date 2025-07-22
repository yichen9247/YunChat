package com.android.yunchat.state

import androidx.compose.runtime.mutableStateOf
import androidx.navigation.NavController
import com.android.yunchat.model.QrcodeScanResultModel
import com.android.yunchat.model.UserInfoModel

object GlobalState {
    val tencentListen = mutableStateOf(false)
    val userInfo = mutableStateOf<UserInfoModel?>(null)
    val navController = mutableStateOf<NavController?>(null)
    val scanCache = mutableStateOf<QrcodeScanResultModel?>(null)
}