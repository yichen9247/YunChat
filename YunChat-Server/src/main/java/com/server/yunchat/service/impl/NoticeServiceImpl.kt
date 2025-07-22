package com.server.yunchat.service.impl

import com.server.yunchat.builder.model.NoticeModel
import com.server.yunchat.plugin.SpringPlugin
import com.server.yunchat.service.NoticeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class NoticeServiceImpl @Autowired constructor(
    private val springPlugin: SpringPlugin
): NoticeService {
    /**
     * @param id 公告id
     * @param title 公告标题
     * @param content 公告内容
     * @param noticeModel 数据模型
     */
    override fun setNotice(
        title: String,
        content: String,
        noticeModel: NoticeModel
    ): NoticeModel {
        noticeModel.title = title
        noticeModel.content = content
        springPlugin.validField(noticeModel)
        return noticeModel
    }
}