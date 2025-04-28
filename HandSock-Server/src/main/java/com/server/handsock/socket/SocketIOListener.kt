package com.server.handsock.socket

import com.corundumstudio.socketio.AckRequest
import com.corundumstudio.socketio.SocketIOClient
import com.corundumstudio.socketio.SocketIOServer
import com.server.handsock.common.utils.ConsoleUtils
import com.server.handsock.common.utils.HandUtils
import com.server.handsock.socket.eventer.OnlineEvent
import com.server.handsock.socket.listener.*
import org.springframework.http.HttpStatus

class SocketIOListener(
    private val onlineEvent: OnlineEvent,
    private val authListener: AuthListener,
    private val forumListener: ForumListener,
    private val adminListener: AdminListener,
    private val checkListener: CheckListener,
    private val updateListener: UpdateListener,
    private val clientListener: ClientListener,
    private val searchListener: SearchListener,
    private val configListener: ConfigListener,
    private val reportListener: ReportListener,
    private val sendingListener: SendingListener,
    private val commentListener: CommentListener
) {
    fun addServerEventListener(server: SocketIOServer) {
        server.addConnectListener {
            onlineEvent.sendUserConnect(
                client = it,
                server = server
            )
        }

        server.addDisconnectListener {
            onlineEvent.sendUserDisconnect(
                client = it,
                server = server
            )
        }

        server.addEventListener("[CLIENT:PING]", Map::class.java) { _: SocketIOClient?, _: Map<*, *>?, ackSender: AckRequest ->
            ackSender.sendAckData(HandUtils.handleResultByCode(HttpStatus.OK, null, "心跳正常"))
        }

        server.addEventListener("[ONLINE:LOGIN]", Map::class.java) { client: SocketIOClient?, data: Map<*, *>?, ackSender: AckRequest ->
            ackSender.sendAckData(java.util.Map.of("code", 200))
            @Suppress("UNCHECKED_CAST")
            val typedData = data as Map<String, Any>?
            onlineEvent.sendUserOnlineLogin(server, client!!, typedData!!)
        }

        try {
            adminListener.addEventListener()
            forumListener.addEventListener()
            clientListener.addEventListener()
            searchListener.addEventListener()
            authListener.addEventListener()
            configListener.addEventListener()
            updateListener.addEventListener()
            reportListener.addEventListener()
            sendingListener.addEventListener()
            commentListener.addEventListener()
            checkListener.addEventListener(HandUtils)
        } catch (e: Exception) {
            ConsoleUtils.printErrorLog(e)
        }
    }
}