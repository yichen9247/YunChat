package com.android.yunchat.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.android.yunchat.screen.login.LoginScreen
import com.android.yunchat.service.instance.activityServiceInstance
import com.android.yunchat.tencent.model.TencentViewModel
import top.chengdongqing.weui.core.ui.theme.WeUITheme

class LoginActivity : ComponentActivity() {
    private val viewModel: TencentViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initActivity()
        setContent {
            WeUITheme {
                LoginScreen()
            }
        }
    }

    /**
     * @name 初始化页面服务
     */
    private fun initActivity() {
        installSplashScreen()
        activityServiceInstance.initYunChatService()
        activityServiceInstance.initTencentService(this)
    }

    @Deprecated("Should use Activity Result API", level = DeprecationLevel.HIDDEN)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        viewModel.handleActivityResult(requestCode, resultCode, data)
    }
}
