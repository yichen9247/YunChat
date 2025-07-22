package com.server.yunchat.socket.listener

import com.corundumstudio.socketio.AckRequest
import com.corundumstudio.socketio.SocketIOClient
import com.server.yunchat.builder.data.SocketScanStatus
import com.server.yunchat.builder.data.SocketUserLogin
import com.server.yunchat.builder.types.EventType
import com.server.yunchat.socket.handler.AuthHandler
import com.server.yunchat.socket.service.SocketEventListener
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class LoginListener @Autowired constructor(private val authHandler: AuthHandler) {
    @SocketEventListener(event = EventType.USER_LOGIN, eventClass = SocketUserLogin::class)
    fun userLogin(client: SocketIOClient, data: SocketUserLogin, ackSender: AckRequest) {
        ackSender.sendAckData(authHandler.handleUserLogin(client, data))
    }

    @SocketEventListener(event = EventType.USER_REGISTER, eventClass = SocketUserLogin::class)
    fun userRegister(client: SocketIOClient, data: SocketUserLogin, ackSender: AckRequest) {
        ackSender.sendAckData(authHandler.handleUserRegister(client, data))
    }

    @SocketEventListener(event = EventType.USER_SCAN_LOGIN, eventClass = SocketScanStatus::class)
    fun userScanLogin(client: SocketIOClient, data: SocketScanStatus, ackSender: AckRequest) {
        ackSender.sendAckData(authHandler.handleUserScanLogin(client))
    }

    @SocketEventListener(event = EventType.USER_SCAN_LOGIN_STATUS, eventClass = SocketScanStatus::class)
    fun userScanLoginStatus(client: SocketIOClient, data: SocketScanStatus, ackSender: AckRequest) {
        ackSender.sendAckData(authHandler.handleGetScanLoginStatus(client, data))
    }
}