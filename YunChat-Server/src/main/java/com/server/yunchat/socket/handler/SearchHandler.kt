package com.server.yunchat.socket.handler

import com.server.yunchat.builder.data.CommonSearchPage
import com.server.yunchat.builder.data.ResultModel
import com.server.yunchat.builder.service.NoticeObjService
import com.server.yunchat.builder.utils.ResultUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class SearchHandler @Autowired constructor(
    private val noticeObjService: NoticeObjService,
) {
    fun handleSearchAllNotice(data: CommonSearchPage): ResultModel {
        return if (data.page == null || data.limit == null || data.page <= 0 || data.limit <= 0)
            ResultUtils.printParamMessage()
        else noticeObjService.searchSystemNotice(
            page = data.page,
            limit = data.limit
        )
    }
}
