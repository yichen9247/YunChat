package com.android.yunchat.request.retrofit.service

import com.android.yunchat.config.AppConfig
import com.android.yunchat.service.instance.encryptServiceInstance
import com.android.yunchat.service.instance.systemServiceInstance
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

internal object RetrofitManager {
    private val headerInterceptor = Interceptor { chain ->
        val originalRequest = chain.request()
        val authorization = encryptServiceInstance.encryptAuthorization()
        val modifiedRequest = originalRequest.newBuilder()
            .header("androidId", systemServiceInstance.getAndroidId())
            .header("authorization", "Bearer $authorization}")
            .build()
        chain.proceed(modifiedRequest)
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(headerInterceptor)
        .build()

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(AppConfig.serverUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}