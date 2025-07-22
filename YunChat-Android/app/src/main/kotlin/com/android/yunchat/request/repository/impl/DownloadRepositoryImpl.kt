package com.android.yunchat.request.repository.impl

import com.android.yunchat.request.repository.DownloadRepository
import com.android.yunchat.request.retrofit.service.RetrofitManager
import com.android.yunchat.request.service.DownloadRequestService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import java.io.IOException

class DownloadRepositoryImpl: DownloadRepository {
    private val downloadRequestService by lazy {
        RetrofitManager.retrofit.create(DownloadRequestService::class.java)
    }

    override suspend fun downloadFile(filename: String): ResponseBody? {
        return withContext(Dispatchers.IO) {
            try {
                downloadRequestService.downloadFile(filename)
            } catch (e: IOException) {
                null
            }
        }
    }
}