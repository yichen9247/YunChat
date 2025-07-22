package com.server.yunchat.service

import com.server.yunchat.builder.data.ControllerUserBind

interface RequestService {
    fun getHitokoto(): String
    fun getWeiboHotSearch(): String
    fun getBilibiliHotSearch(): String
    fun getAttributionByIp(ip: String): String
    fun validTencentLogin(data: ControllerUserBind): Boolean
}