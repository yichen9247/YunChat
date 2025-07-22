package com.android.yunchat.request.model

import androidx.lifecycle.ViewModel
import com.android.yunchat.model.DashboardModel
import com.android.yunchat.model.ResultModel
import com.android.yunchat.request.repository.impl.AdminRequestRepositoryImpl

class AdminRequestViewModel: ViewModel() {
    private val adminRequestRepository by lazy {
        AdminRequestRepositoryImpl()
    }

    suspend fun fetchDashboardData(): ResultModel<DashboardModel>? {
        return adminRequestRepository.fetchDashboardData()
    }

}