package com.android.yunchat.request.repository

import com.android.yunchat.model.DashboardModel
import com.android.yunchat.model.ResultModel

interface AdminRequestRepository {
    suspend fun fetchDashboardData(): ResultModel<DashboardModel>?
}