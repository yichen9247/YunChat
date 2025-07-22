package com.server.yunchat.service

import com.server.yunchat.builder.data.SocketSendMessage
import com.server.yunchat.builder.model.MessageModel

interface ChatService {
    fun sendRoomMessage(message: MessageModel)
    fun setMessageModel(uid: Long, content: String, message: SocketSendMessage, messageModel: MessageModel): MessageModel
}