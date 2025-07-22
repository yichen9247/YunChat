package com.server.yunchat.socket.listener

import com.corundumstudio.socketio.AckRequest
import com.corundumstudio.socketio.SocketIOClient
import com.server.yunchat.builder.data.SocketOnlineModel
import com.server.yunchat.builder.types.EventType
import com.server.yunchat.builder.types.UserAuthType
import com.server.yunchat.builder.utils.HandUtils.handleResultByCode
import com.server.yunchat.client.service.ClientUserService
import com.server.yunchat.service.impl.OnlineServiceImpl
import com.server.yunchat.socket.service.SocketEventListener
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class ClientListener @Autowired constructor(
    private val clientUserService: ClientUserService,
    private val onlineServiceImpl: OnlineServiceImpl
) {
    @SocketEventListener(event = EventType.CLIENT_PING, eventClass = Any::class)
    fun clientPing(client: SocketIOClient, data: Any, ackSender: AckRequest) {
        ackSender.sendAckData(handleResultByCode(HttpStatus.OK, null, "心跳正常"))
    }

    @SocketEventListener(
        event = EventType.ONLINE_LOGIN,
        eventClass = SocketOnlineModel::class,
        permission = UserAuthType.USER_AUTHENTICATION
    )
    fun onlineLogin(client: SocketIOClient, data: SocketOnlineModel, ackSender: AckRequest, uid: Long) {
        ackSender.sendAckData(onlineServiceImpl.handleUserOnlineEvent(uid, data, client))
    }

    @SocketEventListener(
        eventClass = Map::class,
        event = EventType.CLIENT_INIT,
        permission = UserAuthType.USER_AUTHENTICATION
    )
    fun clientInit(client: SocketIOClient, data: Any, ackSender: AckRequest, uid: Long) {
        ackSender.sendAckData(clientUserService.getUserInfo(uid))
    }
}