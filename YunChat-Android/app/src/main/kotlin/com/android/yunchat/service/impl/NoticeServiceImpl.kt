package com.android.yunchat.service.impl

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.android.yunchat.R
import com.android.yunchat.service.NoticeService
import com.android.yunchat.service.instance.systemServiceInstance
import com.xuexiang.xutil.XUtil

/**
 * @name 通知服务实现类
 * @author yichen9247
 */
class NoticeServiceImpl() : NoticeService {

    companion object {
        private const val NOTIFICATION_ID = 1001
        private const val CHANNEL_NAME = "YunChat消息通知"
        private const val CHANNEL_ID = "yunchat_notification_channel"
    }

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
            description = "接收新消息通知"
        }
        val notificationManager = XUtil.getContext().getSystemService(
            NotificationManager::class.java
        )
        notificationManager.createNotificationChannel(channel)
    }

     fun showNotification(title: String, content: String) {
         if (systemServiceInstance.getIsApplicationInForeground()) return
         val context = XUtil.getContext()
         val notification: Notification = NotificationCompat.Builder(context, CHANNEL_ID)
             .setAutoCancel(true)
             .setContentTitle(title)
             .setContentText(content)
             .setSmallIcon(R.drawable.yunchat)
             .setPriority(NotificationCompat.PRIORITY_HIGH)
             .build()
         val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
         notificationManager.notify(NOTIFICATION_ID, notification)
     }
}