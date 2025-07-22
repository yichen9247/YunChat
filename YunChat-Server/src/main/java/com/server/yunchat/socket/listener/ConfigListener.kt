package com.server.yunchat.socket.listener

import com.corundumstudio.socketio.AckRequest
import com.corundumstudio.socketio.SocketIOClient
import com.server.yunchat.admin.service.ServerSystemService
import com.server.yunchat.builder.data.SocketSystemConfig
import com.server.yunchat.builder.types.EventType
import com.server.yunchat.builder.types.UserAuthType
import com.server.yunchat.socket.handler.ConfigHandler
import com.server.yunchat.socket.service.SocketEventListener
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ConfigListener @Autowired constructor(
    private val configHandler: ConfigHandler,
    private val serverSystemService: ServerSystemService
) {
    @SocketEventListener(
        eventClass = Map::class,
        event = EventType.GET_SYSTEM_CONFIG,
        permission = UserAuthType.ADMIN_AUTHENTICATION
    )
    fun getSystemConfig(client: SocketIOClient, data: Map<String, Any>, ackSender: AckRequest) {
        ackSender.sendAckData(serverSystemService.getAllSystemConfig())
    }

    @SocketEventListener(
        eventClass = SocketSystemConfig::class,
        event = EventType.SET_SYSTEM_CONFIG_VALUE,
        permission = UserAuthType.ADMIN_AUTHENTICATION
    )
    fun setSystemConfigValue(client: SocketIOClient, data: SocketSystemConfig, ackSender: AckRequest) {
        ackSender.sendAckData(configHandler.handleSetSystemConfigValue(data))
    }
}