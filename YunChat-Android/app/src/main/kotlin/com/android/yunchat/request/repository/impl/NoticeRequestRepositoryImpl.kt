package com.android.yunchat.request.repository.impl

import com.android.yunchat.model.NoticeItemModel
import com.android.yunchat.model.ResultListModel
import com.android.yunchat.model.ResultModel
import com.android.yunchat.request.repository.NoticeRequestRepository
import com.android.yunchat.request.retrofit.service.RetrofitManager
import com.android.yunchat.request.service.NoticeRequestService
import com.android.yunchat.service.instance.requestServiceInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NoticeRequestRepositoryImpl: NoticeRequestRepository {
    private val noticeRequestService by lazy {
        RetrofitManager.retrofit.create(NoticeRequestService::class.java)
    }

    override suspend fun fetchNoticeList(): ResultModel<ResultListModel<NoticeItemModel>>? {
        return withContext(Dispatchers.IO) {
            try {
                noticeRequestService.fetchNoticeList()
            } catch (e: Exception) {
                requestServiceInstance.handleRequestError(e)
                null
            }
        }
    }
}