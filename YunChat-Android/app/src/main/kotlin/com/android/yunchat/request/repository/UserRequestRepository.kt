package com.android.yunchat.request.repository

import com.android.yunchat.model.ResultModel
import com.android.yunchat.model.TencentLoginModel
import com.android.yunchat.model.UserAuthModel
import com.android.yunchat.model.UserAuthSendModel
import com.android.yunchat.model.UserBindModel
import com.android.yunchat.model.UserInfoModel
import com.android.yunchat.model.UserInfoUpdateModel
import com.android.yunchat.model.UserScanLoginModel
import retrofit2.http.Body
import retrofit2.http.Query

interface UserRequestRepository {
    suspend fun fetchUserBind(): ResultModel<UserBindModel>?
    suspend fun fetchUserQQBind(
        @Body data: TencentLoginModel,
        @Query("status") status: Int
    ): ResultModel<*>?
    suspend fun fetchUserInfo(uid: Long): ResultModel<UserInfoModel>?
    suspend fun fetchUserScanLogin(data: UserScanLoginModel): ResultModel<*>?
    suspend fun fetchUserScanStatus(data: UserScanLoginModel): ResultModel<*>?
    suspend fun fetchUpdatePassword(data: UserInfoUpdateModel): ResultModel<*>?
    suspend fun fetchUpdateUserNick(data: UserInfoUpdateModel): ResultModel<*>?
    suspend fun fetchUpdateUserAvatar(data: UserInfoUpdateModel): ResultModel<*>?
    suspend fun fetchUserLogin(data: UserAuthSendModel): ResultModel<UserAuthModel>?
    suspend fun fetchUserQQLogin(data: TencentLoginModel): ResultModel<UserAuthModel>?
    suspend fun fetchUserRegister(data: UserAuthSendModel): ResultModel<UserAuthModel>?
}