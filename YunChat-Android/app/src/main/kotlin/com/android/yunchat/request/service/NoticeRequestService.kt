package com.android.yunchat.request.service

import com.android.yunchat.model.NoticeItemModel
import com.android.yunchat.model.ResultListModel
import com.android.yunchat.model.ResultModel
import retrofit2.http.GET

/**
 * @name 公告相关接口
 * @author yichen9247
 */
interface NoticeRequestService {
    @GET("/api/android/notice/search/all")
    suspend fun fetchNoticeList(): ResultModel<ResultListModel<NoticeItemModel>>
}