package com.android.yunchat.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.FragmentActivity
import com.android.yunchat.screen.user.UserScreen
import com.android.yunchat.service.instance.activityServiceInstance
import com.android.yunchat.tencent.model.TencentViewModel
import top.chengdongqing.weui.core.ui.theme.WeUITheme

class UserActivity: FragmentActivity() {
    private val viewModel: TencentViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initActivity()
        setContent {
            WeUITheme {
                UserScreen(intent)
            }
        }
    }

    /**
     * @name 初始化页面服务
     */
    private fun initActivity() {
        installSplashScreen()
        activityServiceInstance.initYunChatService()
    }

    @Deprecated("Should use Activity Result API", level = DeprecationLevel.HIDDEN)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        viewModel.handleActivityResult(requestCode, resultCode, data)
    }
}