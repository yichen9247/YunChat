package com.android.yunchat.request.repository

import com.android.yunchat.model.GroupInfoModel
import com.android.yunchat.model.ResultModel
import com.android.yunchat.model.SocketGroupOperate

interface GroupRequestRepository {
    suspend fun fetchGroupList(): ResultModel<List<GroupInfoModel>>?
    suspend fun fetchJoinGroup(data: SocketGroupOperate): ResultModel<Any>?
    suspend fun fetchExitGroup(data: SocketGroupOperate): ResultModel<Any>?
    suspend fun fetchSearchGroupList(value: String): ResultModel<List<GroupInfoModel>>?
}