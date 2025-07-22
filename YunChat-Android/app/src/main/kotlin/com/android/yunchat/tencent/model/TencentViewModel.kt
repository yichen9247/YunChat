package com.android.yunchat.tencent.model

import android.app.Activity
import android.content.Intent
import androidx.lifecycle.ViewModel
import com.android.yunchat.state.GlobalState
import com.android.yunchat.tencent.helper.TencentLoginHelper
import com.android.yunchat.tencent.state.TencentState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class TencentViewModel : ViewModel() {
    private val _state = MutableStateFlow<TencentState>(TencentState.Idle)
    private val _loginState = MutableStateFlow<TencentState>(TencentState.Idle)

    val state: StateFlow<TencentState> = _state
    val loginState: StateFlow<TencentState> = _loginState

    private val loginHelper by lazy {
        TencentLoginHelper.create(
            onError = { message ->
                _loginState.value = TencentState.Error(message)
            },
            onCancel = {
                _loginState.value = TencentState.Cancelled
            },
            onSuccess = { data ->
                _loginState.value = TencentState.Success(data)
            }
        )
    }

    fun handleLogin(activity: Activity) {
        loginHelper.login(activity)
        _loginState.value = TencentState.Loading
    }

    fun handleActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        loginHelper.handleActivityResult(requestCode, resultCode, data)
    }

    fun clearTencentListenFlag() {
        GlobalState.tencentListen.value = false
    }
}