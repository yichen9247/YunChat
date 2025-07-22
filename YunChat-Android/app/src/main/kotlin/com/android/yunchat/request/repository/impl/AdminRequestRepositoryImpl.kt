package com.android.yunchat.request.repository.impl

import com.android.yunchat.model.DashboardModel
import com.android.yunchat.model.ResultModel
import com.android.yunchat.request.repository.AdminRequestRepository
import com.android.yunchat.request.retrofit.service.RetrofitManager
import com.android.yunchat.request.service.AdminRequestService
import com.android.yunchat.service.instance.requestServiceInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AdminRequestRepositoryImpl: AdminRequestRepository {
    private val adminRequestService by lazy {
        RetrofitManager.retrofit.create(AdminRequestService::class.java)
    }

    override suspend fun fetchDashboardData(): ResultModel<DashboardModel>? {
        return withContext(Dispatchers.IO) {
            try {
                adminRequestService.fetchDashboardData()
            } catch (e: Exception) {
                requestServiceInstance.handleRequestError(e)
                null
            }
        }
    }
}