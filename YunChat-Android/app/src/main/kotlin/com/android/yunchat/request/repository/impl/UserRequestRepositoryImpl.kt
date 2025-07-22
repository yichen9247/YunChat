package com.android.yunchat.request.repository.impl

import com.android.yunchat.model.ResultModel
import com.android.yunchat.model.TencentLoginModel
import com.android.yunchat.model.UserAuthEncryptModel
import com.android.yunchat.model.UserAuthModel
import com.android.yunchat.model.UserAuthSendModel
import com.android.yunchat.model.UserBindModel
import com.android.yunchat.model.UserInfoModel
import com.android.yunchat.model.UserInfoUpdateModel
import com.android.yunchat.model.UserScanLoginModel
import com.android.yunchat.request.repository.UserRequestRepository
import com.android.yunchat.request.retrofit.service.RetrofitManager
import com.android.yunchat.request.service.UserRequestService
import com.android.yunchat.service.instance.encryptServiceInstance
import com.android.yunchat.service.instance.requestServiceInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRequestRepositoryImpl: UserRequestRepository {
    private val userRequestService by lazy {
        RetrofitManager.retrofit.create(UserRequestService::class.java)
    }

    override suspend fun fetchUserLogin(data: UserAuthSendModel): ResultModel<UserAuthModel>? {
        return withContext(Dispatchers.IO) {
            try {
                userRequestService.fetchUserLogin(UserAuthEncryptModel(
                    data = encryptServiceInstance.encryptLogin(
                        username = data.username,
                        password = data.password
                    )
                ))
            } catch (e: Exception) {
                requestServiceInstance.handleRequestError(e)
                null
            }
        }
    }

    override suspend fun fetchUserRegister(data: UserAuthSendModel): ResultModel<UserAuthModel>? {
        return withContext(Dispatchers.IO) {
            try {
                userRequestService.fetchUserRegister(UserAuthEncryptModel(
                    data = encryptServiceInstance.encryptLogin(
                        username = data.username,
                        password = data.password
                    )
                ))
            } catch (e: Exception) {
                requestServiceInstance.handleRequestError(e)
                null
            }
        }
    }

    override suspend fun fetchUserQQLogin(data: TencentLoginModel): ResultModel<UserAuthModel>? {
        return withContext(Dispatchers.IO) {
            try {
                userRequestService.fetchUserQQLogin(data)
            } catch (e: Exception) {
                requestServiceInstance.handleRequestError(e)
                null
            }
        }
    }

    override suspend fun fetchUpdateUserNick(data: UserInfoUpdateModel): ResultModel<*>? {
        return withContext(Dispatchers.IO) {
            try {
                userRequestService.fetchUpdateUserNick(data)
            } catch (e: Exception) {
                requestServiceInstance.handleRequestError(e)
                null
            }
        }
    }

    override suspend fun fetchUpdateUserAvatar(data: UserInfoUpdateModel): ResultModel<*>? {
        return withContext(Dispatchers.IO) {
            try {
                userRequestService.fetchUpdateUserAvatar(data)
            } catch (e: Exception) {
                requestServiceInstance.handleRequestError(e)
                null
            }
        }
    }

    override suspend fun fetchUpdatePassword(data: UserInfoUpdateModel): ResultModel<*>? {
        return withContext(Dispatchers.IO) {
            try {
                userRequestService.fetchUpdatePassword(data)
            } catch (e: Exception) {
                requestServiceInstance.handleRequestError(e)
                null
            }
        }
    }

    override suspend fun fetchUserBind(): ResultModel<UserBindModel>? {
        return withContext(Dispatchers.IO) {
            try {
                userRequestService.fetchUserBind()
            } catch (e: Exception) {
                requestServiceInstance.handleRequestError(e)
                null
            }
        }
    }

    override suspend fun fetchUserQQBind(data: TencentLoginModel, status: Int): ResultModel<*>? {
        return withContext(Dispatchers.IO) {
            try {
                userRequestService.fetchUserQQBind(data, status)
            } catch (e: Exception) {
                requestServiceInstance.handleRequestError(e)
                null
            }
        }
    }

    override suspend fun fetchUserInfo(uid: Long): ResultModel<UserInfoModel>? {
        return withContext(Dispatchers.IO) {
            try {
                userRequestService.fetchUserInfo(uid)
            } catch (e: Exception) {
                requestServiceInstance.handleRequestError(e)
                null
            }
        }
    }

    override suspend fun fetchUserScanLogin(data: UserScanLoginModel): ResultModel<*>? {
        return withContext(Dispatchers.IO) {
            try {
                userRequestService.fetchUserScanLogin(data)
            } catch (e: Exception) {
                requestServiceInstance.handleRequestError(e)
                null
            }
        }
    }

    override suspend fun fetchUserScanStatus(data: UserScanLoginModel): ResultModel<*>? {
        return withContext(Dispatchers.IO) {
            try {
                userRequestService.fetchUserScanStatus(data)
            } catch (e: Exception) {
                requestServiceInstance.handleRequestError(e)
                null
            }
        }
    }
}