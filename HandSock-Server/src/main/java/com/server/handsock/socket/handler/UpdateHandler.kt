package com.server.handsock.socket.handler

import com.corundumstudio.socketio.AckRequest
import com.corundumstudio.socketio.SocketIOClient
import com.server.handsock.client.service.ClientUserService
import com.server.handsock.common.data.SocketUserUpdate
import com.server.handsock.common.utils.HandUtils
import com.server.handsock.service.AuthService
import com.server.handsock.service.ClientService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class UpdateHandler @Autowired constructor(
    private val authService: AuthService,
    private val clientService: ClientService,
    private val clientUserService: ClientUserService
) {
    fun handleEditUserInfo(client: SocketIOClient, data: SocketUserUpdate, ackSender: AckRequest, editType: String) {
        ackSender.sendAckData(authService.validClientStatusBySocket(client) {
            try {
                when (editType) {
                    "USER:NICK" -> {
                        if (data.nick.isNullOrEmpty())
                            HandUtils.printParamError()
                        else clientUserService.editForNick(
                            nick = data.nick,
                            uid = clientService.getRemoteUID(client)
                        )
                    }
                    "USER:AVATAR" -> {
                        if (data.path.isNullOrEmpty())
                            HandUtils.printParamError()
                        else clientUserService.editForAvatar(
                            path = data.path,
                            uid = clientService.getRemoteUID(client)
                        )
                    }
                    "USER:USERNAME" -> {
                        if (data.username.isNullOrEmpty())
                            HandUtils.printParamError()
                        else clientUserService.editForUserName(
                            username = data.username,
                            uid = clientService.getRemoteUID(client)
                        )
                    }
                    "USER:PASSWORD" -> {
                        if (data.password.isNullOrEmpty())
                            HandUtils.printParamError()
                        else clientUserService.editForPassword(
                            password = data.password,
                            uid = clientService.getRemoteUID(client)
                        )
                    }
                    else -> HandUtils.handleResultByCode(HttpStatus.NOT_ACCEPTABLE, null, "无效的编辑类型")
                }
            } catch (e: Exception) {
                HandUtils.printErrorLog(e)
            }
        })
    }
}
