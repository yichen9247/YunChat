package com.android.yunchat

import android.app.Activity
import android.app.Application
import android.os.Bundle
import coil.ImageLoader
import coil.disk.DiskCache
import com.tencent.mmkv.MMKV
import com.tencent.tauth.Tencent

class YunChat : Application() {
    companion object {
        lateinit var tencent: Tencent
        lateinit var instance: YunChat
        lateinit var imageLoader: ImageLoader
        private set
    }

    override fun onCreate() {
        initCoil()
        instance = this
        super.onCreate()
        MMKV.initialize(this)
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
            override fun onActivityStarted(activity: Activity) {}
            override fun onActivityResumed(activity: Activity) {}
            override fun onActivityPaused(activity: Activity) {}
            override fun onActivityStopped(activity: Activity) {}
            override fun onActivityDestroyed(activity: Activity) {}
            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
        })
    }

    private fun initCoil() {
        imageLoader = ImageLoader.Builder(this)
            .diskCache {
                DiskCache.Builder()
                    .directory(cacheDir.resolve("coil_disk_cache"))
                    .maxSizeBytes(500 * 1024 * 1024)
                    .build()
            }
            .build()
    }
}