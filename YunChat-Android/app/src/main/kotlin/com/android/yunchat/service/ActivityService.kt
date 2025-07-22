package com.android.yunchat.service

import android.content.Context
import com.android.yunchat.model.GroupInfoModel

interface ActivityService {
    fun initYunChatService()
    fun intentActivityBack(context: Context)
    fun initTencentService(context: Context)
    fun initChatBatteryPermission(context: Context)

    fun intentChatActivity(
        context: Context,
        groupInfo: GroupInfoModel
    )
}