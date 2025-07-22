package com.android.yunchat.request.repository.impl

import com.android.yunchat.model.GroupInfoModel
import com.android.yunchat.model.ResultModel
import com.android.yunchat.model.SocketGroupOperate
import com.android.yunchat.request.repository.GroupRequestRepository
import com.android.yunchat.request.retrofit.service.RetrofitManager
import com.android.yunchat.request.service.GroupRequestService
import com.android.yunchat.service.instance.requestServiceInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GroupRequestRepositoryImpl: GroupRequestRepository {
    private val groupRequestService by lazy {
        RetrofitManager.retrofit.create(GroupRequestService::class.java)
    }

    override suspend fun fetchJoinGroup(data: SocketGroupOperate): ResultModel<Any>? {
        return withContext(Dispatchers.IO) {
            try {
                groupRequestService.fetchJoinGroup(data)
            } catch (e: Exception) {
                requestServiceInstance.handleRequestError(e)
                null
            }
        }
    }

    override suspend fun fetchExitGroup(data: SocketGroupOperate): ResultModel<Any>? {
        return withContext(Dispatchers.IO) {
            try {
                groupRequestService.fetchExitGroup(data)
            } catch (e: Exception) {
                requestServiceInstance.handleRequestError(e)
                null
            }
        }
    }

    override suspend fun fetchSearchGroupList(value: String): ResultModel<List<GroupInfoModel>>? {
        return withContext(Dispatchers.IO) {
            try {
                groupRequestService.fetchSearchGroupList(value)
            } catch (e: Exception) {
                requestServiceInstance.handleRequestError(e)
                null
            }
        }
    }

    override suspend fun fetchGroupList(): ResultModel<List<GroupInfoModel>>? {
        return withContext(Dispatchers.IO) {
            try {
                groupRequestService.fetchGroupList()
            } catch (e: Exception) {
                requestServiceInstance.handleRequestError(e)
                null
            }
        }
    }
}