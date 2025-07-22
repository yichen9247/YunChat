package com.android.yunchat.core

import android.util.Log
import com.android.yunchat.activity.LoginActivity
import com.android.yunchat.service.instance.systemServiceInstance
import com.xuexiang.xutil.XUtil
import top.chengdongqing.weui.core.utils.showToast

class CrashHandler : Thread.UncaughtExceptionHandler {
    private var mDefaultHandler: Thread.UncaughtExceptionHandler? = null

    init {
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler(this)
    }

    override fun uncaughtException(thread: Thread, ex: Throwable) {
        if (!handleException(ex)) mDefaultHandler?.uncaughtException(thread, ex)
    }

    private fun handleException(ex: Throwable): Boolean {
        ex.printStackTrace()
        Log.d("YunChat-LOG", ex.message.toString())
        XUtil.getContext().showToast(when(ex.message.toString()) {
            "navigation destination net_err is not a direct child of this NavGraph" -> {
                "网络异常导致崩溃"
            }
            else -> {
                "应用崩溃，即将重启"
            }
        })
        Thread.sleep(2000)
        systemServiceInstance.restartThisApplication(LoginActivity::class.java)
        return true
    }
}