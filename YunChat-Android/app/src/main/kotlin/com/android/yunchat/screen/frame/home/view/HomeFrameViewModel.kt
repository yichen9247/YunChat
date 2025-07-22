package com.android.yunchat.screen.frame.home.view

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.android.yunchat.model.GroupInfoModel
import com.android.yunchat.request.model.GroupRequestViewModel
import com.android.yunchat.service.instance.requestServiceInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * @name 主页ViewModel
 * @author yichen9247
 */
class HomeFrameViewModel: ViewModel() {
    val groupList = mutableStateOf<List<GroupInfoModel>>(emptyList())

    /**
     * @name 获取群聊列表
     */
    suspend fun getGroupList() = withContext(Dispatchers.IO) {
        GroupRequestViewModel().fetchGroupList()?.let {
            requestServiceInstance.checkResponseResult(
                response = it,
                message = "获取群聊列表失败",
                onSuccess = {
                    it.data?.let { groupList.value = it }
                }
            )
        }
    }
}