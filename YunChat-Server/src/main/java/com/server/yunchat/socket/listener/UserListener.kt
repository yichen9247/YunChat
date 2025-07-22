package com.server.yunchat.socket.listener

import com.corundumstudio.socketio.AckRequest
import com.corundumstudio.socketio.SocketIOClient
import com.server.yunchat.builder.data.UserUpdateAvatar
import com.server.yunchat.builder.data.UserUpdateNick
import com.server.yunchat.builder.data.UserUpdatePassword
import com.server.yunchat.builder.types.EventType
import com.server.yunchat.builder.types.UserAuthType
import com.server.yunchat.client.service.ClientUserService
import com.server.yunchat.socket.handler.UserHandler
import com.server.yunchat.socket.service.SocketEventListener
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class UserListener @Autowired constructor(
    private val userHandler: UserHandler,
    private val clientUserService: ClientUserService
) {
    @SocketEventListener(event = EventType.GET_USER_LIST, eventClass = Any::class)
    fun getUserList(client: SocketIOClient, data: Any, ackSender: AckRequest) {
        ackSender.sendAckData(clientUserService.queryAllUser())
    }

    @SocketEventListener(
        event = EventType.EDIT_USER_NICK,
        eventClass = UserUpdateNick::class,
        permission = UserAuthType.USER_AUTHENTICATION
    )
    fun editUserNick(client: SocketIOClient, data: UserUpdateNick, ackSender: AckRequest, uid: Long) {
        ackSender.sendAckData(userHandler.handleUpdateUserNick(uid, data))
    }

    @SocketEventListener(
        event = EventType.EDIT_USER_AVATAR,
        eventClass = UserUpdateAvatar::class,
        permission = UserAuthType.USER_AUTHENTICATION
    )
    fun editUserAvatar(client: SocketIOClient, data: UserUpdateAvatar, ackSender: AckRequest, uid: Long) {
        ackSender.sendAckData(userHandler.handleUpdateUserAvatar(uid, data))
    }

    @SocketEventListener(
        event = EventType.EDIT_USER_PASSWORD,
        eventClass = UserUpdatePassword::class,
        permission = UserAuthType.USER_AUTHENTICATION
    )
    fun editUserPassword(client: SocketIOClient, data: UserUpdatePassword, ackSender: AckRequest, uid: Long) {
        ackSender.sendAckData(userHandler.handleUpdateUserPassword(uid, data))
    }
}