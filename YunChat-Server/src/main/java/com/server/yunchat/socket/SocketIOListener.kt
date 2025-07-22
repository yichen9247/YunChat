package com.server.yunchat.socket

import com.corundumstudio.socketio.SocketIOServer
import com.server.yunchat.service.impl.OnlineServiceImpl
import org.springframework.stereotype.Service

@Service
class SocketIOListener(private val onlineServiceImpl: OnlineServiceImpl) {
    fun addServerEventListener(server: SocketIOServer) {
        server.addConnectListener {
            onlineServiceImpl.clientGoOnline(it)
        }

        server.addDisconnectListener {
            onlineServiceImpl.userGoOffline(it)
        }
    }
}