package com.android.yunchat.service.impl

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PowerManager
import androidx.compose.runtime.mutableLongStateOf
import com.android.yunchat.YunChat
import com.android.yunchat.activity.ChatActivity
import com.android.yunchat.config.AppConfig
import com.android.yunchat.core.CrashHandler
import com.android.yunchat.model.GroupInfoModel
import com.android.yunchat.service.ActivityService
import com.android.yunchat.service.instance.dialogServiceInstance
import com.tencent.tauth.Tencent

/**
 * @name 页面服务实现类
 * @author yichen9247
 */
class ActivityServiceImpl: ActivityService {
    /**
     * @name 初始化全局异常捕获
     */
    override fun initYunChatService() {
        CrashHandler()
    }

    /**
     * @name 返回上一页
     * @param context 上下文
     */
    override fun intentActivityBack(context: Context) {
        (context as Activity).finish()
    }

    /**
     * @name 初始化腾讯登录服务
     * @param context UI上下文
     */
    override fun initTencentService(context: Context) {
        Tencent.setIsPermissionGranted(true)
        val authorities = "${context.packageName}.provider"
        YunChat.tencent = Tencent.createInstance(AppConfig.tencentAppId, context, authorities)
    }

    /**
     * @name 跳转界面
     * @param context 原页面
     * @param activity 新页面
     */
     fun intentActivityAboutContext(
        context: Context,
        activity: Class<*>,
        extrasBuilder: Bundle.() -> Unit = {}
    ) {
        val currentTime = System.currentTimeMillis()
        if ((currentTime - lastLinkClickTime.longValue) > 500) {
            lastLinkClickTime.longValue = currentTime
            val intent = Intent(context, activity).apply {
                this.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                putExtras(Bundle().apply(extrasBuilder))
            }
            context.startActivity(intent)
        }
    }

    /**
     * @name 检查电池的优化设置
     */
    override fun initChatBatteryPermission(context: Context) {
        if (isIgnoringBatteryOptimizations(context)) return
        dialogServiceInstance.openBatteryOptimizationDialog(context)
    }

    /**
     * @name 跳转聊天界面
     * @param context 上下文
     * @param groupInfo 群聊信息
     */
    override fun intentChatActivity(
        context: Context,
        groupInfo: GroupInfoModel
    ) {
        intentActivityAboutContext(context, ChatActivity::class.java) {
            putInt("gid", groupInfo.gid)
            putString("name", groupInfo.name)
            putString("notice", groupInfo.notice)
            putInt("member", groupInfo.member.size)
        }
    }

    /**
     * @name 检查是否已忽略电池优化
     * @param context 上下文
     * @return Boolean
     */
    private fun isIgnoringBatteryOptimizations(context: Context): Boolean {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        return powerManager.isIgnoringBatteryOptimizations(context.packageName)
    }

    companion object {
        private val lastLinkClickTime = mutableLongStateOf(0L)
    }
}