package com.server.yunchat.socket.listener

import com.corundumstudio.socketio.AckRequest
import com.corundumstudio.socketio.SocketIOClient
import com.server.yunchat.builder.data.CommonSearchPage
import com.server.yunchat.builder.types.EventType
import com.server.yunchat.builder.types.UserAuthType
import com.server.yunchat.client.service.ClientUserService
import com.server.yunchat.socket.handler.SearchHandler
import com.server.yunchat.socket.service.SocketEventListener
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class SearchListener @Autowired constructor(
    private val searchHandler: SearchHandler,
    private val clientUserService: ClientUserService,
) {
    @SocketEventListener(
        event = EventType.SEARCH_NOTICE,
        eventClass = CommonSearchPage::class,
        permission = UserAuthType.USER_AUTHENTICATION
    )
    fun searchNotice(client: SocketIOClient, data: CommonSearchPage, ackSender: AckRequest) {
        ackSender.sendAckData(searchHandler.handleSearchAllNotice(data))
    }

    @SocketEventListener(event = EventType.SEARCH_USER_ALL, eventClass = Any::class)
    fun searchAllUser(client: SocketIOClient, data: Any, ackSender: AckRequest) {
        ackSender.sendAckData(clientUserService.queryAllUser())
    }
}