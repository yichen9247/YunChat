package com.android.yunchat.request.service

import com.android.yunchat.model.ResultModel
import com.android.yunchat.model.TencentLoginModel
import com.android.yunchat.model.UserAuthEncryptModel
import com.android.yunchat.model.UserAuthModel
import com.android.yunchat.model.UserBindModel
import com.android.yunchat.model.UserInfoModel
import com.android.yunchat.model.UserInfoUpdateModel
import com.android.yunchat.model.UserScanLoginModel
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * @name 用户相关接口
 * @author yichen9247
 */
interface UserRequestService {
    @POST("/api/android/user/login")
    suspend fun fetchUserLogin(
        @Body data: UserAuthEncryptModel
    ): ResultModel<UserAuthModel>

    @POST("/api/android/user/register")
    suspend fun fetchUserRegister(
        @Body data: UserAuthEncryptModel
    ): ResultModel<UserAuthModel>

    @POST("/api/android/user/login/qq")
    suspend fun fetchUserQQLogin(
        @Body data: TencentLoginModel,
    ): ResultModel<UserAuthModel>

    @POST("/api/android/user/login/scan")
    suspend fun fetchUserScanLogin(
        @Body data: UserScanLoginModel
    ): ResultModel<*>

    @POST("/api/android/user/login/scan/status")
    suspend fun fetchUserScanStatus(
        @Body data: UserScanLoginModel
    ): ResultModel<*>

    @GET("/api/android/user/info")
    suspend fun fetchUserInfo(
        @Query("uid") uid: Long
    ): ResultModel<UserInfoModel>

    @POST("/api/android/user/edit/nick")
    suspend fun fetchUpdateUserNick(
        @Body data: UserInfoUpdateModel
    ): ResultModel<*>

    @POST("/api/android/user/edit/avatar")
    suspend fun fetchUpdateUserAvatar(
        @Body data: UserInfoUpdateModel
    ): ResultModel<*>

    @POST("/api/android/user/edit/password")
    suspend fun fetchUpdatePassword(
        @Body data: UserInfoUpdateModel
    ): ResultModel<*>

    @GET("/api/android/user/bind")
    suspend fun fetchUserBind(): ResultModel<UserBindModel>

    @POST("/api/android/user/bind/qq")
    suspend fun fetchUserQQBind(
        @Body data: TencentLoginModel,
        @Query("status") status: Int
    ): ResultModel<*>
}