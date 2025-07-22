package com.server.yunchat.socket.listener

import com.corundumstudio.socketio.AckRequest
import com.corundumstudio.socketio.SocketIOClient
import com.server.yunchat.builder.data.SocketGetMessageList
import com.server.yunchat.builder.data.SocketSendMessage
import com.server.yunchat.builder.types.EventType
import com.server.yunchat.builder.types.UserAuthType
import com.server.yunchat.socket.handler.AIChatHandler
import com.server.yunchat.socket.handler.ChatHandler
import com.server.yunchat.socket.service.SocketEventListener
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ChatListener @Autowired constructor(
    private val chatHandler: ChatHandler,
    private val aiChatHandler: AIChatHandler
) {
    @SocketEventListener(
        event = EventType.SEND_MESSAGE,
        eventClass = SocketSendMessage::class,
        permission = UserAuthType.USER_AUTHENTICATION
    )
    fun sendMessage(client: SocketIOClient, data: SocketSendMessage, ackSender: AckRequest, uid: Long) {
        ackSender.sendAckData(chatHandler.handleSendMessage(uid, client, data))
    }

    @SocketEventListener(
        event = EventType.SEND_AI_MESSAGE,
        eventClass = SocketSendMessage::class,
        permission = UserAuthType.USER_AUTHENTICATION
    )
    fun sendAiMessage(client: SocketIOClient, data: SocketSendMessage, ackSender: AckRequest, uid: Long) {
        ackSender.sendAckData(aiChatHandler.handleAIChatMessage(uid, client, data, ackSender))
    }

    @SocketEventListener(
        eventClass = Any::class,
        event = EventType.RESET_AI_MESSAGE,
        permission = UserAuthType.USER_AUTHENTICATION
    )
    fun resetAiMessage(client: SocketIOClient, data: Any, ackSender: AckRequest, uid: Long) {
        ackSender.sendAckData(aiChatHandler.handleResetAiMessage(uid))
    }

    @SocketEventListener(event = EventType.GET_MESSAGE_LIST, eventClass = SocketGetMessageList::class)
    fun getMessageList(client: SocketIOClient, data: SocketGetMessageList, ackSender: AckRequest, uid: Long) {
        ackSender.sendAckData(chatHandler.getMessageList(data))
    }
}