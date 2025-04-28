package com.server.handsock.admin.service

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.server.handsock.common.dao.MessageDao
import com.server.handsock.common.model.MessageModel
import com.server.handsock.common.utils.HandUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class ServerChatService @Autowired constructor(private val messageDao: MessageDao) {
     fun getChatContent(sid: String): Map<String, Any> {
        val messageModel = messageDao.selectOne(QueryWrapper<MessageModel>().eq("sid", sid))
        return if (messageModel != null) {
            HandUtils.handleResultByCode(HttpStatus.OK, messageModel, "获取成功")
        } else HandUtils.handleResultByCode(HttpStatus.NOT_ACCEPTABLE, null, "内容已被删除")
    }

    fun getChatList(page: Int, limit: Int): Map<String, Any> {
        val pageObj = Page<MessageModel>(page.toLong(), limit.toLong())
        val wrapper = QueryWrapper<MessageModel>().orderByDesc("time")
        val queryResult = messageDao.selectPage(pageObj, wrapper)
        return HandUtils.handleResultByCode(HttpStatus.OK,  mapOf(
            "total" to queryResult.total,
            "items" to queryResult.records
        ), "获取成功")
    }

    fun deleteChat(sid: String): Map<String, Any> {
        val messageModel = messageDao.selectOne(QueryWrapper<MessageModel>().eq("sid", sid))
        return if (messageModel != null) {
            if (messageDao.deleteById(sid) > 0) {
                HandUtils.handleResultByCode(HttpStatus.OK, null, "删除成功")
            } else HandUtils.handleResultByCode(HttpStatus.INTERNAL_SERVER_ERROR, null, "删除失败")
        } else HandUtils.handleResultByCode(HttpStatus.NOT_ACCEPTABLE, null, "消息已被删除")
    }

    fun clearAllChatHistory() {
        if (messageDao.delete(null) > 0) {
            HandUtils.handleResultByCode(HttpStatus.OK, null, "清空聊天记录成功")
        } else HandUtils.handleResultByCode(HttpStatus.INTERNAL_SERVER_ERROR, null, "清空聊天记录成功失败")
    }
}
