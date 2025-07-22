package com.android.yunchat.tencent.state

import com.android.yunchat.model.TencentLoginModel

sealed class TencentState {
    object Idle : TencentState()
    object Loading : TencentState()
    object Cancelled : TencentState()
    data class Error(val message: String) : TencentState()
    data class Success(val data: TencentLoginModel) : TencentState()
}