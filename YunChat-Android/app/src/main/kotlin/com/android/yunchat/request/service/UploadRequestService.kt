package com.android.yunchat.request.service

import com.android.yunchat.model.ResultModel
import com.android.yunchat.model.UploadResultModel
import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

/**
 * @name 上传相关接口
 * @author yichen9247
 */
interface UploadRequestService {
    @Multipart
    @POST("/api/upload/avatar")
    suspend fun uploadAvatar(
        @Part file: MultipartBody.Part,
    ): ResultModel<UploadResultModel>

    @Multipart
    @POST("/api/upload/video")
    suspend fun uploadVideo(
        @Part file: MultipartBody.Part,
    ): ResultModel<UploadResultModel>

    @Multipart
    @POST("/api/upload/image")
    suspend fun uploadImage(
        @Part file: MultipartBody.Part,
    ): ResultModel<UploadResultModel>

    @Multipart
    @POST("/api/upload/file")
    suspend fun uploadFile(
        @Part file: MultipartBody.Part,
    ): ResultModel<UploadResultModel>
}