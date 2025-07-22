package com.android.yunchat.service.impl

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.core.net.toUri
import com.android.yunchat.model.UpdateInfoModel
import com.android.yunchat.service.SystemService
import com.king.app.updater.AppUpdater
import com.king.app.updater.callback.UpdateCallback
import com.king.app.updater.http.OkHttpManager
import com.xuexiang.xutil.XUtil
import top.chengdongqing.weui.core.utils.deleteFile
import top.chengdongqing.weui.core.utils.showToast
import java.io.File

/**
 * @name 系统服务实现类
 * @author yichen9247
 */
class SystemServiceImpl: SystemService {
    /**
     * @name 获取设备Android ID
     * @return String
     */
    @SuppressLint("HardwareIds")
    override fun getAndroidId(): String {
        val context = XUtil.getContext()
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }

    /**
     * @name 清除应用缓存
     */
    override fun clearApplicationCache() {
        val context = XUtil.getContext()
        context.showToast(if (deleteFile(context.cacheDir)) "清除缓存成功" else "清除缓存失败")
    }

    /**
     * @name 重启当前应用
     * @description 适用于应用卡死等情况
     */
    override fun restartThisApplication(activity: Class<*>) {
        val context = XUtil.getContext()
        val intent = Intent(context, activity)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
        Runtime.getRuntime().exit(0)
    }

    /**
     * @name 在浏览器中打开链接
     * @param url 链接
     */
    override fun openUrlInBrowser(url: String) {
        XUtil.getContext().startActivity(Intent(Intent.ACTION_VIEW, url.toUri()).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
    }

    /**
     * @name 获取当前应用版本号
     * @return String
     */
    override fun getApplicationVersionName(): String {
        val context = XUtil.getContext()
        return context.packageManager.getPackageInfo(context.packageName, 0).versionName
    }

    /**
     * @name 更新应用
     * @param data 更新信息
     */
    override fun updateApplication(data: UpdateInfoModel) {
        val context = XUtil.getContext()
        val appUpdater = AppUpdater.Builder(context)
            .setUrl(data.download)
            .build()
        appUpdater.setHttpManager(OkHttpManager.getInstance()).setUpdateCallback(object : UpdateCallback {
            override fun onCancel() {}
            override fun onFinish(file: File) {}
            override fun onStart(url: String) {}
            override fun onError(e: Exception) {}
            override fun onDownloading(isDownloading: Boolean) {}
            override fun onProgress(progress: Long, total: Long, isChanged: Boolean) {}
        }).start()
    }

    /**
     * @name 通过设备打开文件
     * @param uri 文件URI
     * @description 可以打开的文件类型取决于设备
     */
    override fun openFileWithDevice(uri: Uri) {
        val context = XUtil.getContext()
        val mimeType = context.contentResolver.getType(uri) ?: "*/*"

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, mimeType)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        try {
            context.startActivity(intent)
        } catch (_: ActivityNotFoundException) {
            XUtil.getContext().showToast("没有应用可以打开此文件")
        }
    }

    /**
     * @name 获取应用缓存大小
     * @return String
     */
    override fun getApplicationCacheSize(): String {
        val dir = XUtil.getContext().cacheDir
        return formatSize(calculateSize(dir))
    }

    /**
     * @name 检查应用是否在前台
     */
    @SuppressLint("ServiceCast")
    override fun getIsApplicationInForeground(): Boolean {
        val context = XUtil.getContext()
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningAppProcesses = activityManager.runningAppProcesses ?: return false
        for (processInfo in runningAppProcesses) {
            if (processInfo.processName == context.packageName) {
                return processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
            }
        }
        return false
    }

    // 计算程序的缓存大小
    private fun formatSize(sizeBytes: Long): String {
        val units = listOf("B", "KB", "MB", "GB")
        var size = sizeBytes.toDouble()
        var unitIndex = 0

        while (size > 1024 && unitIndex < units.size - 1) {
            size /= 1024
            unitIndex++
        }
        return "%.2f%s".format(size, units[unitIndex])
    }

    // 计算程序的缓存大小
    private fun calculateSize(file: File): Long {
        var size = 0L
        if (file.isDirectory) {
            file.listFiles()?.forEach { child ->
                size += calculateSize(child)
            }
        } else size = file.length()
        return size
    }
}