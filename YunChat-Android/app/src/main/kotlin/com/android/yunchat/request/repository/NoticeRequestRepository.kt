package com.android.yunchat.request.repository

import com.android.yunchat.model.NoticeItemModel
import com.android.yunchat.model.ResultListModel
import com.android.yunchat.model.ResultModel

interface NoticeRequestRepository {
    suspend fun fetchNoticeList(): ResultModel<ResultListModel<NoticeItemModel>>?
}