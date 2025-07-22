package com.android.yunchat.screen.notice.view

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.yunchat.model.NoticeItemModel
import com.android.yunchat.model.ResultListModel
import com.android.yunchat.request.model.NoticeRequestViewModel
import com.android.yunchat.service.instance.requestServiceInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * @name 公告ViewModel
 * @author yichen9247
 */
class NoticeViewModel: ViewModel() {
    val isLoading = mutableStateOf(true)
    val noticeData = mutableStateOf<ResultListModel<NoticeItemModel>>(ResultListModel(
        total = 0,
        items = emptyList()
    ))

    init {
        getNoticeList()
    }

    private fun getNoticeList() {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                NoticeRequestViewModel().fetchNoticeList()
            }
            result?.let { response ->
                requestServiceInstance.checkResponseResult(
                    response = response,
                    message = "获取后台数据失败",
                    onSuccess = {
                        response.data?.let {
                            noticeData.value = it
                            isLoading.value = false
                        }
                    }
                )
            }
        }
    }
}