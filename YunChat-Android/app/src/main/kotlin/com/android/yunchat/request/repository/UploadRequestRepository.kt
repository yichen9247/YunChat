package com.android.yunchat.request.repository

import com.android.yunchat.model.ResultModel
import com.android.yunchat.model.UploadResultModel
import okhttp3.MultipartBody

interface UploadRequestRepository {
    suspend fun uploadFile(file: MultipartBody.Part):ResultModel<UploadResultModel>?
    suspend fun uploadImage(file: MultipartBody.Part):ResultModel<UploadResultModel>?
    suspend fun uploadVideo(file: MultipartBody.Part):ResultModel<UploadResultModel>?
    suspend fun uploadAvatar(file: MultipartBody.Part):ResultModel<UploadResultModel>?
}