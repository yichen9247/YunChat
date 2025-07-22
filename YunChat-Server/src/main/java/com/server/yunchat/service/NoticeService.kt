package com.server.yunchat.service

import com.server.yunchat.builder.model.NoticeModel

interface NoticeService {
    fun setNotice(title: String, content: String, noticeModel: NoticeModel): NoticeModel
}