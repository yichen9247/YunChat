package com.android.yunchat.request.service

import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Streaming
import retrofit2.http.Url

/**
 * @name 下载相关接口
 * @author yichen9247
 */
interface DownloadRequestService {
    @GET @Streaming
    suspend fun downloadFile(
        @Url filename: String
    ): ResponseBody
}