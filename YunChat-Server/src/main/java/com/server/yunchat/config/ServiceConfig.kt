package com.server.yunchat.config

import com.corundumstudio.socketio.AuthorizationListener
import com.corundumstudio.socketio.AuthorizationResult
import com.corundumstudio.socketio.SocketIOServer
import com.corundumstudio.socketio.store.RedissonStoreFactory
import com.server.yunchat.builder.props.YunChatProps
import com.server.yunchat.builder.utils.ConsoleUtils
import com.server.yunchat.builder.utils.GlobalService
import com.server.yunchat.service.impl.AuthServiceImpl
import com.server.yunchat.socket.SocketIOListener
import org.redisson.api.RedissonClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import kotlin.system.exitProcess

@Configuration
open class ServiceConfig @Autowired constructor(
    private val yunChatProps: YunChatProps,
    private val redissonClient: RedissonClient,
    private val authServiceImpl: AuthServiceImpl,
    private val socketIOListener: SocketIOListener
) {
    private val config = com.corundumstudio.socketio.Configuration().apply {
        origin = null
        isRandomSession = true
        isNeedClientAuth = true
        port = yunChatProps.port
        isAddVersionHeader = true
        hostname = yunChatProps.host
        isWebsocketCompression = true
        pingTimeout = yunChatProps.pingTimeout
        pingInterval = yunChatProps.pingInterval
        upgradeTimeout = yunChatProps.upgradeTimeout
        authorizationListener = AuthorizationListener {
            authServiceImpl.validSocketAuthorization(it)
        }
        storeFactory = RedissonStoreFactory(redissonClient)
    }

    @Bean
    open fun socketIOServer(redissonClient: RedissonClient?): SocketIOServer {
        val socketIOServer = SocketIOServer(config)
        if (yunChatProps.secretKey.length != 16) {
            ConsoleUtils.printErrorLog("YunChat.SecretKey长度必须为16位")
            exitProcess(0)
        }
        GlobalService.yunchatProps = yunChatProps
        GlobalService.socketIOServer = socketIOServer
        socketIOListener.addServerEventListener(socketIOServer)
        return socketIOServer
    }
}
