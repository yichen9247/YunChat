package com.android.yunchat.request.repository

import okhttp3.ResponseBody

interface DownloadRepository {
    suspend fun downloadFile(filename: String): ResponseBody?
}