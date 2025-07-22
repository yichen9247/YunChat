package com.android.yunchat.request.service

import com.android.yunchat.model.DashboardModel
import com.android.yunchat.model.ResultModel
import retrofit2.http.GET

/**
 * @name 管理相关接口
 * @author yichen9247
 */
interface AdminRequestService {
    @GET("/api/admin/dash/data")
    suspend fun fetchDashboardData(): ResultModel<DashboardModel>
}