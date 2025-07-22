package com.android.yunchat.config

object AppConfig {
    const val animationSpec: Long = 150 // 基础动画时长
    const val tencentAppId: String = "..." // 腾讯QQ互联AppId
    const val socketVersion: String = "2.3.2-B21" // 后端版本
    const val secretKey: String = "..." // 服务保护密钥（配置为16位长度的字符串）
    const val socketUrl: String = "http://localhost:5120" // 通信地址
    const val serverUrl: String = "http://localhost:8081" // 后端地址
}