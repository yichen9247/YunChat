package com.android.yunchat.tencent.helper

import android.app.Activity
import android.content.Intent
import com.android.yunchat.YunChat
import com.android.yunchat.model.TencentLoginModel
import com.google.gson.Gson
import com.tencent.connect.common.Constants
import com.tencent.tauth.DefaultUiListener
import com.tencent.tauth.IUiListener
import com.tencent.tauth.Tencent
import com.tencent.tauth.UiError

class TencentLoginHelper private constructor(
    private val onError: (String) -> Unit,
    private val onCancel: () -> Unit,
    private val onSuccess: (TencentLoginModel) -> Unit
) {
    companion object {
        fun create(
            onCancel: () -> Unit,
            onError: (String) -> Unit,
            onSuccess: (TencentLoginModel) -> Unit,
        ): TencentLoginHelper {
            return TencentLoginHelper(onError, onCancel, onSuccess)
        }
    }

    private var currentListener: BaseUiListener? = null

    fun login(activity: Activity) {
        try {
            Tencent.setIsPermissionGranted(true)
            val tencent = YunChat.tencent

            val tencentListener = object : DefaultUiListener() {
                override fun onComplete(response: Any?) {
                    val loginModel = Gson().fromJson(response.toString(), TencentLoginModel::class.java)
                    onSuccess(loginModel)
                }

                override fun onError(e: UiError?) {
                    onError("Login exception: $e")
                }

                override fun onCancel() {
                    null
                }
            }
            tencent.login(activity, "all", tencentListener)
        } catch (e: Exception) {
            onError("Login exception: ${e.message}")
        }
    }

    fun handleActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == Constants.REQUEST_LOGIN) {
            Tencent.onActivityResultData(requestCode, resultCode, data, currentListener)
        }
        currentListener = null
    }

    abstract inner class BaseUiListener : IUiListener {
        override fun onWarning(code: Int) {}
    }
}