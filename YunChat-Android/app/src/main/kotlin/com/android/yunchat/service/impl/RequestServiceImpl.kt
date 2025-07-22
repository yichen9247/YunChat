package com.android.yunchat.service.impl

import com.android.yunchat.model.ResultModel
import com.android.yunchat.service.RequestService
import com.xuexiang.xutil.XUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.launch
import top.chengdongqing.weui.core.utils.showToast
import java.io.IOException

/**
 * @name 请求服务实现类
 * @author yichen9247
 */
class RequestServiceImpl: RequestService {
    /**
     * @name 处理请求错误
     * @param error 错误信息
     */
    override fun handleRequestError(error: Exception) {
        when (error) {
            is IOException -> "网络连接异常"
            is TimeoutCancellationException -> "请求超时"
            else -> null
        }?.let {
            XUtil.getContext().showToast(it)
        }
    }

    /**
     * @name 检查请求结果
     * @param response 请求结果
     * @param message 请求失败提示
     * @param onSuccess 请求成功回调
     */
    override fun checkResponseResult(
        message: String,
        onSuccess: () -> Unit,
        response: ResultModel<*>
    ) {
        CoroutineScope(Dispatchers.Main).launch {
            when(response.code) {
                200 -> onSuccess()
                else -> XUtil.getContext().showToast(response.message?: message)
            }
        }
    }
}