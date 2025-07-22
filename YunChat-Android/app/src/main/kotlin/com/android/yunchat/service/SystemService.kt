package com.android.yunchat.service

import android.net.Uri
import com.android.yunchat.model.UpdateInfoModel

interface SystemService {
    fun getAndroidId(): String
    fun clearApplicationCache()
    fun openFileWithDevice(uri: Uri)
    fun openUrlInBrowser(url: String)
    fun getApplicationCacheSize(): String
    fun getApplicationVersionName(): String
    fun getIsApplicationInForeground(): Boolean
    fun updateApplication(data: UpdateInfoModel)
    fun restartThisApplication(activity: Class<*>)
}