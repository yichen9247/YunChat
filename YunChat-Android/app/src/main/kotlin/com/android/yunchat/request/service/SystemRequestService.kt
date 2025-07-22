package com.android.yunchat.request.service

import com.android.yunchat.model.CheckUpdateOperate
import com.android.yunchat.model.ResultModel
import com.android.yunchat.model.UpdateInfoModel
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * @name 系统相关接口
 * @author yichen9247
 */
interface SystemRequestService {
    @POST("/api/android/system/update")
    suspend fun fetchCheckUpdate(
        @Body data: CheckUpdateOperate
    ): ResultModel<UpdateInfoModel>
}