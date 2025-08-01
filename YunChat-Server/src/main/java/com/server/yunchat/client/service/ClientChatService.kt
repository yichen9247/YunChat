package com.server.yunchat.client.service

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.server.yunchat.builder.dao.MessageDao
import com.server.yunchat.builder.data.ResultModel
import com.server.yunchat.builder.data.SocketSendMessage
import com.server.yunchat.builder.model.MessageModel
import com.server.yunchat.builder.utils.HandUtils
import com.server.yunchat.service.impl.ChatServiceImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class ClientChatService @Autowired constructor(
    private val messageDao: MessageDao,
    private val chatServiceImpl: ChatServiceImpl
) {
    fun sendChatMessage(uid: Long, content: String, message: SocketSendMessage): ResultModel {
        val allowType = listOf("file", "text", "image", "video", "invite")
        if (!allowType.contains(message.type)) return HandUtils.handleResultByCode(HttpStatus.BAD_REQUEST, null,"不支持的消息类型")
        return try {
            val messageModel = MessageModel()
            val result = chatServiceImpl.setMessageModel(uid, content, message, messageModel)
            if (messageDao.insert(messageModel) > 0) {
                chatServiceImpl.sendRoomMessage(messageModel)
                HandUtils.handleResultByCode(HttpStatus.OK, result, "发送成功")
            } else HandUtils.handleResultByCode(HttpStatus.INTERNAL_SERVER_ERROR, null, "发送失败")
        } catch (e: Exception) {
            HandUtils.handleResultByCode(HttpStatus.BAD_REQUEST, null, e.message.toString())
        }
    }

    fun getMessageList(obj: Int, tar: Long): ResultModel {
        val wrapper = QueryWrapper<MessageModel>()
        wrapper.orderByAsc("time").eq("obj", obj).eq("tar", tar)
        return HandUtils.handleResultByCode(HttpStatus.OK, messageDao.selectList(wrapper), "获取成功")
    }
}
