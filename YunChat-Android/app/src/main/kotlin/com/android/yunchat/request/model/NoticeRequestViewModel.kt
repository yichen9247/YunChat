package com.android.yunchat.request.model

import androidx.lifecycle.ViewModel
import com.android.yunchat.model.NoticeItemModel
import com.android.yunchat.model.ResultListModel
import com.android.yunchat.model.ResultModel
import com.android.yunchat.request.repository.impl.NoticeRequestRepositoryImpl

class NoticeRequestViewModel: ViewModel() {
    private val noticeRequestRepository by lazy {
        NoticeRequestRepositoryImpl()
    }

    suspend fun fetchNoticeList(): ResultModel<ResultListModel<NoticeItemModel>>? {
        return noticeRequestRepository.fetchNoticeList()
    }
}