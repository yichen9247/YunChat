package com.server.yunchat.socket

import com.server.yunchat.socket.listener.*
import com.server.yunchat.socket.service.SocketEventService
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Service

@Service
class SocketInitializer(
    private val roomListener: RoomListener,
    private val loginListener: LoginListener,
    private val userListener: UserListener,
    private val chatListener: ChatListener,
    private val groupListener: GroupListener,
    private val adminListener: AdminListener,
    private val reportListener: ReportListener,
    private val configListener: ConfigListener,
    private val clientListener: ClientListener,
    private val searchListener: SearchListener,
    private val socketEventService: SocketEventService
) {
    @PostConstruct
    fun init() {
        socketEventService.addSocketEventListener(roomListener)
        socketEventService.addSocketEventListener(loginListener)
        socketEventService.addSocketEventListener(userListener)
        socketEventService.addSocketEventListener(groupListener)
        socketEventService.addSocketEventListener(chatListener)
        socketEventService.addSocketEventListener(adminListener)
        socketEventService.addSocketEventListener(reportListener)
        socketEventService.addSocketEventListener(configListener)
        socketEventService.addSocketEventListener(clientListener)
        socketEventService.addSocketEventListener(searchListener)
    }
}