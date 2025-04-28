package com.server.handsock.socket.handler

import com.corundumstudio.socketio.AckRequest
import com.corundumstudio.socketio.SocketIOClient
import com.server.handsock.client.service.ClientUserService
import com.server.handsock.common.utils.HandUtils
import com.server.handsock.service.AuthService
import com.server.handsock.service.ClientService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class ClientHandler @Autowired constructor(
    private val authService: AuthService,
    private val clientService: ClientService,
    private val clientUserService: ClientUserService
) {
    fun handleClientInit(client: SocketIOClient, ackRequest: AckRequest) {
        ackRequest.sendAckData(authService.validClientStatusBySocket(client) {
            HandUtils.handleResultByCode(HttpStatus.OK, clientUserService.queryUserInfo(clientService.getRemoteUID(client)), "获取成功")
        })
    }
}
