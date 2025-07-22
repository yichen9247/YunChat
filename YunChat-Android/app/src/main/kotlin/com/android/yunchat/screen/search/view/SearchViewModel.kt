package com.android.yunchat.screen.search.view

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.android.yunchat.model.GroupInfoModel
import com.android.yunchat.request.model.GroupRequestViewModel
import com.android.yunchat.service.instance.requestServiceInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * @name 搜索ViewModel
 * @author yichen9247
 */
class SearchViewModel: ViewModel() {
    val groupList = mutableStateOf<List<GroupInfoModel>>(emptyList())

    /**
     * @name 获取群聊列表
     */
    suspend fun searchGroupList(value: String) = withContext(Dispatchers.IO) {
        GroupRequestViewModel().fetchSearchGroupList(value)?.let { list ->
            requestServiceInstance.checkResponseResult(
                response = list,
                message = "获取群聊列表失败",
                onSuccess = {
                    list.data?.let {
                        groupList.value = list.data
                    }
                }
            )
        }
    }
}