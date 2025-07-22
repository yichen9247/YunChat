package com.android.yunchat.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.android.yunchat.screen.agent.AgentScreen
import com.android.yunchat.service.instance.activityServiceInstance
import top.chengdongqing.weui.core.ui.theme.WeUITheme

class AgentActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initActivity()
        initChatService()
        setContent {
            WeUITheme {
                AgentScreen()
            }
        }
    }

    /**
     * @name 初始化聊天服务
     */
    private fun initChatService() {
        activityServiceInstance.initChatBatteryPermission(this)
    }

    /**
     * @name 初始化页面服务
     */
    private fun initActivity() {
        installSplashScreen()
        activityServiceInstance.initYunChatService()
    }
}