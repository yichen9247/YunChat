package com.android.yunchat.tencent

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.yunchat.model.TencentLoginModel
import com.android.yunchat.state.GlobalState
import com.android.yunchat.tencent.model.TencentViewModel
import com.android.yunchat.tencent.state.TencentState
import top.chengdongqing.weui.core.utils.showToast

@Composable
fun TencentCallback(
    onSuccess: (data: TencentLoginModel) -> Unit,
    tencentViewModel: TencentViewModel = viewModel()
) {
    val context = LocalContext.current
    val loginState by tencentViewModel.loginState.collectAsState()

    LaunchedEffect(loginState) {
        when (val state = loginState) {
            is TencentState.Success -> {
                if (GlobalState.tencentListen.value) onSuccess(state.data)
                tencentViewModel.clearTencentListenFlag()
            }
            is TencentState.Error -> {
                if (GlobalState.tencentListen.value) {
                    context.showToast("登录失败")
                }
                tencentViewModel.clearTencentListenFlag()
            }
            else -> {}
        }
    }
}