package com.android.yunchat.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.android.yunchat.screen.voice.VoiceScreen
import com.android.yunchat.service.instance.activityServiceInstance
import top.chengdongqing.weui.core.ui.theme.WeUITheme

class  VoiceActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initActivity()
        setContent {
            WeUITheme {
                VoiceScreen()
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
}