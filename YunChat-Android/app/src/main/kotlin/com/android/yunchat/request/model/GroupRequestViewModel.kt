package com.android.yunchat.request.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.yunchat.model.GroupInfoModel
import com.android.yunchat.model.ResultModel
import com.android.yunchat.model.SocketGroupOperate
import com.android.yunchat.request.repository.impl.GroupRequestRepositoryImpl
import com.android.yunchat.service.instance.requestServiceInstance
import com.android.yunchat.service.instance.sharedHomeFrameViewModel
import com.xuexiang.xutil.XUtil
import kotlinx.coroutines.launch
import top.chengdongqing.weui.core.utils.showToast

class GroupRequestViewModel: ViewModel() {
    private val groupRequestRepository by lazy {
        GroupRequestRepositoryImpl()
    }

    suspend fun fetchGroupList(): ResultModel<List<GroupInfoModel>>? {
        return groupRequestRepository.fetchGroupList()
    }

    suspend fun fetchSearchGroupList(value: String): ResultModel<List<GroupInfoModel>>? {
        return groupRequestRepository.fetchSearchGroupList(value)
    }

    suspend fun fetchJoinGroup(gid: Int) {
        groupRequestRepository.fetchJoinGroup(SocketGroupOperate(
            gid = gid
        ))?.let {
            requestServiceInstance.checkResponseResult(
                response = it,
                message = "加入群聊失败",
                onSuccess = {
                    viewModelScope.launch {
                        sharedHomeFrameViewModel.getGroupList()
                        XUtil.getContext().showToast(it.message?: "加入群聊成功")
                    }
                }
            )
        }
    }

    suspend fun fetchExitGroup(gid: Int, onSuccess: () -> Unit) {
        groupRequestRepository.fetchExitGroup(SocketGroupOperate(
            gid = gid
        ))?.let {
            requestServiceInstance.checkResponseResult(
                response = it,
                message = "退出群聊失败",
                onSuccess = {
                    viewModelScope.launch {
                        sharedHomeFrameViewModel.getGroupList()
                        XUtil.getContext().showToast(it.message?: "退出群聊成功")
                        onSuccess()
                    }
                }
            )
        }
    }
}