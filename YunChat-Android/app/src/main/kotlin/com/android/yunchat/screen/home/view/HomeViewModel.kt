package com.android.yunchat.screen.home.view

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.yunchat.model.QrcodeScanResultModel
import com.android.yunchat.request.model.SystemRequestViewModel
import com.android.yunchat.request.model.UserRequestViewModel
import com.android.yunchat.service.instance.dialogServiceInstance
import com.android.yunchat.service.instance.requestServiceInstance
import com.android.yunchat.state.GlobalState
import com.google.gson.Gson
import com.xuexiang.xutil.XUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import top.chengdongqing.weui.core.ui.components.toast.ToastState
import top.chengdongqing.weui.core.utils.showToast

/**
 * @name 主页ViewModel
 * @author yichen9247
 */
class HomeViewModel: ViewModel() {
    /**
     * @name 处理扫码结果
     * @param content 扫码结果
     * @param content 提示状态
     */
    fun handleScanResult(
        content: String,
        toastState: ToastState
    ) {
        val context = XUtil.getContext()
        try {
            val gson = Gson()
            val resultModel = gson.fromJson(content, QrcodeScanResultModel::class.java)
            GlobalState.scanCache.value = resultModel
            viewModelScope.launch {
                when(resultModel.type) {
                    "login" -> {
                        getUserScanStatus(resultModel.qid!!, toastState)
                    }
                    else -> context.showToast("二维码类型错误")
                }
            }
        } catch (_: Exception) {
            context.showToast("非本渠道二维码")
        }
    }

    /**
     * @name 获取用户扫码状态
     * @param qid 扫码id
     * @param toastState 提示状态
     */
    private fun getUserScanStatus(
        qid: String,
        toastState: ToastState,
    ) {
        val userRequestViewModel = UserRequestViewModel()
        userRequestViewModel.showLoadingToast(toastState)
        viewModelScope.launch {
            try {
                delay(200)
                UserRequestViewModel().fetchUserScanStatus(qid)
            } catch (e: Exception) {
                requestServiceInstance.handleRequestError(e)
            } finally {
                toastState.hide()
            }
        }
    }

    fun checkApplicationUpdate(
        context: Context,
        successToast: Boolean = false
    ) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                SystemRequestViewModel().fetchCheckUpdate()
            }
            result.let {
                when(it?.code) {
                    200 -> if (successToast) context.showToast("已是最新版本")
                    201 -> dialogServiceInstance.openUpdateApplicationDialog(
                        context = context,
                        data = result?.data!!,
                    )
                    else -> context.showToast("获取更新信息失败")
                }
            }
        }
    }
}