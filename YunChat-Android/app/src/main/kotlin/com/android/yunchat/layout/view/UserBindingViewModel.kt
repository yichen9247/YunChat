package com.android.yunchat.layout.view

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.yunchat.model.TencentLoginModel
import com.android.yunchat.model.UserBindModel
import com.android.yunchat.request.model.UserRequestViewModel
import com.android.yunchat.service.instance.requestServiceInstance
import com.android.yunchat.service.instance.storageServiceInstance
import com.xuexiang.xutil.XUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import top.chengdongqing.weui.core.utils.showToast

/**
 * @name 绑定ViewModel
 * @author yichen9247
 */
class UserBindingViewModel: ViewModel() {
    val bingData = mutableStateOf(UserBindModel(
        qq = false
    ))

    init {
        getUserBindData()
    }

    private fun getUserBindData() {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                UserRequestViewModel().fetchUserBind()
            }
            result?.let { response ->
                requestServiceInstance.checkResponseResult(
                    response = response,
                    message = "获取后台数据失败",
                    onSuccess = {
                        response.data?.let {
                            bingData.value = it
                        }
                    }
                )
            }
        }
    }

    fun fetchUserQQBind(
        onDismiss: () -> Unit,
        data: TencentLoginModel
    ) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                UserRequestViewModel().fetchUserQQBind(
                    status = 1,
                    data = TencentLoginModel(
                        openid = data.openid,
                        access_token = data.access_token
                    )
                )
            }
            result?.let { response ->
                val context = XUtil.getContext()
                requestServiceInstance.checkResponseResult(
                    response = response,
                    message = "绑定失败",
                    onSuccess = {
                        onDismiss()
                        context.showToast("绑定成功")
                    }
                )
            }
        }
    }

    fun fetchUserUnQQBind(onDismiss: () -> Unit) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                UserRequestViewModel().fetchUserQQBind(
                    status = 0,
                    data = TencentLoginModel("un", "un")
                )
            }
            result?.let { response ->
                val context = XUtil.getContext()
                requestServiceInstance.checkResponseResult(
                    response = response,
                    message = "解除绑定失败",
                    onSuccess = {
                        onDismiss()
                        context.showToast("解除绑定成功")
                        storageServiceInstance.mmkv.remove("openid")
                        storageServiceInstance.mmkv.remove("access_token")
                    }
                )
            }
        }
    }
}