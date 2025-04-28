package com.server.handsock.config

import com.corundumstudio.socketio.SocketIOServer
import com.corundumstudio.socketio.store.RedissonStoreFactory
import com.server.handsock.common.props.HandProp
import com.server.handsock.common.utils.ConsoleUtils
import com.server.handsock.common.utils.GlobalService
import com.server.handsock.socket.SocketIOListener
import com.server.handsock.socket.eventer.OnlineEvent
import com.server.handsock.socket.listener.*
import org.redisson.api.RedissonClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import kotlin.system.exitProcess

@Configuration
open class ServiceConfig @Autowired constructor(
    private val handProp: HandProp,
    private val onlineEvent: OnlineEvent,
    private val checkListener: CheckListener,
    @param:Lazy private val authListener: AuthListener,
    @param:Lazy private val adminListener: AdminListener,
    @param:Lazy private val forumListener: ForumListener,
    @param:Lazy private val reportListener: ReportListener,
    @param:Lazy private val clientListener: ClientListener,
    @param:Lazy private val searchListener: SearchListener,
    @param:Lazy private val configListener: ConfigListener,
    @param:Lazy private val updateListener: UpdateListener,
    @param:Lazy private val commentListener: CommentListener,
    @param:Lazy private val sendingListener: SendingListener,
) {
    private val config = com.corundumstudio.socketio.Configuration()

    @Bean
    open fun socketIOServer(redissonClient: RedissonClient?): SocketIOServer {
        config.port = handProp.port
        config.origin = handProp.origin
        config.hostname = handProp.host
        config.pingTimeout = handProp.pingTimeout
        config.pingInterval = handProp.pingInterval
        config.upgradeTimeout = handProp.upgradeTimeout
        config.storeFactory = RedissonStoreFactory(redissonClient)
        val socketIOServer = SocketIOServer(config)

        if (handProp.secretKey == null || handProp.secretKey!!.length != 16) {
            ConsoleUtils.printErrorLog("HandSock.SecretKey长度必须为16位")
            exitProcess(0)
        }

        GlobalService.handsockProps = handProp
        GlobalService.socketIOServer = socketIOServer

        SocketIOListener(
            onlineEvent = onlineEvent,
            adminListener = adminListener, checkListener = checkListener, clientListener = clientListener,
            searchListener = searchListener, sendingListener = sendingListener, configListener = configListener,
            updateListener = updateListener, reportListener = reportListener, authListener = authListener,
            forumListener = forumListener, commentListener = commentListener
        ).addServerEventListener(socketIOServer)
        return socketIOServer
    }
}
