package com.android.yunchat.request.repository.impl

import com.android.yunchat.model.ResultModel
import com.android.yunchat.model.UploadResultModel
import com.android.yunchat.request.repository.UploadRequestRepository
import com.android.yunchat.request.retrofit.service.RetrofitManager
import com.android.yunchat.request.service.UploadRequestService
import com.android.yunchat.service.instance.requestServiceInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody

class UploadRequestRepositoryImpl: UploadRequestRepository {
    private val uploadRequestService by lazy {
        RetrofitManager.retrofit.create(UploadRequestService::class.java)
    }

    override suspend fun uploadFile(file: MultipartBody.Part): ResultModel<UploadResultModel>? {
        return withContext(Dispatchers.IO) {
            try {
                uploadRequestService.uploadFile(file)
            } catch (e: Exception) {
                requestServiceInstance.handleRequestError(e)
                null
            }
        }
    }

    override suspend fun uploadImage(file: MultipartBody.Part): ResultModel<UploadResultModel>? {
        return withContext(Dispatchers.IO) {
            try {
                uploadRequestService.uploadImage(file)
            } catch (e: Exception) {
                requestServiceInstance.handleRequestError(e)
                null
            }
        }
    }

    override suspend fun uploadAvatar(file: MultipartBody.Part): ResultModel<UploadResultModel>? {
        return withContext(Dispatchers.IO) {
            try {
                uploadRequestService.uploadAvatar(file)
            } catch (e: Exception) {
                requestServiceInstance.handleRequestError(e)
                null
            }
        }
    }

    override suspend fun uploadVideo(file: MultipartBody.Part): ResultModel<UploadResultModel>? {
        return withContext(Dispatchers.IO) {
            try {
                uploadRequestService.uploadVideo(file)
            } catch (e: Exception) {
                requestServiceInstance.handleRequestError(e)
                null
            }
        }
    }
}