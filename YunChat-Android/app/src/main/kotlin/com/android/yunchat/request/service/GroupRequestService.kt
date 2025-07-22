package com.android.yunchat.request.service

import com.android.yunchat.model.GroupInfoModel
import com.android.yunchat.model.ResultModel
import com.android.yunchat.model.SocketGroupOperate
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * @name 群聊相关接口
 * @author yichen9247
 */
interface GroupRequestService {
    @GET("/api/android/group/list")
    suspend fun fetchGroupList(): ResultModel<List<GroupInfoModel>>

    @POST("/api/android/group/join")
    suspend fun fetchJoinGroup(
        @Body data: SocketGroupOperate
    ): ResultModel<Any>

    @POST("/api/android/group/exit")
    suspend fun fetchExitGroup(
        @Body data: SocketGroupOperate
    ): ResultModel<Any>

    @GET("/api/android/group/search")
    suspend fun fetchSearchGroupList(
        @Query("value") value: String
    ): ResultModel<List<GroupInfoModel>>
}