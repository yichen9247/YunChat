package com.server.handsock.client.service

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.server.handsock.client.man.ClientChatManage
import com.server.handsock.common.dao.MessageDao
import com.server.handsock.common.model.MessageModel
import com.server.handsock.common.utils.ConsoleUtils
import com.server.handsock.common.utils.HandUtils
import com.server.handsock.common.utils.IDGenerator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class ClientChatService @Autowired constructor(private val messageDao: MessageDao) {
    fun insertChatMessage(type: String, uid: Long, gid: Long, address: String, content: String): Map<String, Any> {
        if (content.length > 200) return HandUtils.handleResultByCode(HttpStatus.NOT_ACCEPTABLE, null, "消息过长")
        val messageModel = MessageModel()
        val result = ClientChatManage(HandUtils, ConsoleUtils, IDGenerator).insertChatMessage(messageModel, type, uid, gid, address, content)
        return if (messageDao.insert(messageModel) > 0) {
            HandUtils.handleResultByCode(HttpStatus.OK, result, "发送成功")
        } else HandUtils.handleResultByCode(HttpStatus.INTERNAL_SERVER_ERROR, null, "发送失败")
    }

    fun searchAllChatHistory(gid: Long): Map<String, Any> {
        val wrapper = QueryWrapper<MessageModel>()
        wrapper.orderByAsc("time").eq("gid", gid)
        return HandUtils.handleResultByCode(HttpStatus.OK, messageDao.selectList(wrapper), "获取成功")
    }
}
