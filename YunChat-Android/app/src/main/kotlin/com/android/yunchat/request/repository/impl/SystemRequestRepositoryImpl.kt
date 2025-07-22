package com.android.yunchat.request.repository.impl

import com.android.yunchat.model.CheckUpdateOperate
import com.android.yunchat.model.ResultModel
import com.android.yunchat.model.UpdateInfoModel
import com.android.yunchat.request.repository.SystemRequestRepository
import com.android.yunchat.request.retrofit.service.RetrofitManager
import com.android.yunchat.request.service.SystemRequestService
import com.android.yunchat.service.instance.requestServiceInstance
import com.xuexiang.xutil.app.AppUtils.getAppVersionName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SystemRequestRepositoryImpl: SystemRequestRepository {
    private val systemRequestService by lazy {
        RetrofitManager.retrofit.create(SystemRequestService::class.java)
    }

    override suspend fun fetchCheckUpdate(): ResultModel<UpdateInfoModel>? {
        return withContext(Dispatchers.IO) {
            try {
                systemRequestService.fetchCheckUpdate(CheckUpdateOperate(
                    version = getAppVersionName()
                ))
            } catch (e: Exception) {
                requestServiceInstance.handleRequestError(e)
                null
            }
        }
    }
}