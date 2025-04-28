package com.server.handsock.socket.handler

import com.corundumstudio.socketio.AckRequest
import com.corundumstudio.socketio.SocketIOClient
import com.corundumstudio.socketio.SocketIOServer
import com.server.handsock.admin.service.ServerSystemService
import com.server.handsock.client.service.ClientUserService
import com.server.handsock.common.utils.ConsoleUtils
import com.server.handsock.common.utils.HandUtils
import com.server.handsock.common.utils.IDGenerator
import com.server.handsock.service.AuthService
import com.server.handsock.service.ClientService
import com.server.handsock.service.TokenService
import com.server.handsock.socket.eventer.OnlineEvent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class AuthHandler @Autowired constructor(
    private val authService: AuthService,
    private val onlineEvent: OnlineEvent,
    private val tokenService: TokenService,
    private val clientService: ClientService,
    private val clientUserService: ClientUserService,
    private val serverSystemService: ServerSystemService
) {
    fun handleUserLogin(client: SocketIOClient, data: Map<String?, Any>, ackSender: AckRequest) {
        try {
            if (!onlineEvent.checkClient(HandUtils.encodeStringToMD5(client.sessionId.toString()))) {
                ackSender.sendAckData(HandUtils.handleResultByCode(HttpStatus.FORBIDDEN, null, "禁止访问"))
            } else {
                val username = data["username"]?.toString()
                val password = data["password"]?.toString()
                ackSender.sendAckData(
                    if (username.isNullOrEmpty() || password.isNullOrEmpty())
                        HandUtils.printParamError()
                    else clientUserService.loginUser(
                        username = username,
                        password = password,
                        address = clientService.getRemoteAddress(client)
                    )
                )
            }
        } catch (e: Exception) {
            ackSender.sendAckData(HandUtils.printErrorLog(e))
        }
    }

    fun handleUserLogout(client: SocketIOClient, server: SocketIOServer?, ackSender: AckRequest) {
        ackSender.sendAckData(authService.validClientStatusBySocket(client) {
            if (!onlineEvent.checkClient(HandUtils.encodeStringToMD5(client.sessionId.toString()))) {
                HandUtils.handleResultByCode(HttpStatus.FORBIDDEN, null, "禁止访问")
            } else {
                onlineEvent.sendUserDisconnect(server!!, client)
                ConsoleUtils.printInfoLog(
                    "User Logout ${clientService.getRemoteAddress(client)} ${clientService.getRemoteUID(client)}"
                )
            }
        })
    }

    fun handleUserRegister(client: SocketIOClient, data: Map<String?, Any>, ackSender: AckRequest) {
        try {
            if (!onlineEvent.checkClient(HandUtils.encodeStringToMD5(client.sessionId.toString()))) {
                ackSender.sendAckData(HandUtils.handleResultByCode(HttpStatus.FORBIDDEN, null, "禁止访问"))
            } else {
                if (serverSystemService.getSystemKeyStatus("register")) {
                    val username = data["username"]?.toString()
                    val password = data["password"]?.toString()
                    ackSender.sendAckData(
                        if (username == null || password == null)
                            HandUtils.printParamError()
                        else clientUserService.registerUser(
                            username = username,
                            password = password,
                            address = clientService.getRemoteAddress(client)
                        )
                    )
                } else ackSender.sendAckData(HandUtils.handleResultByCode(HttpStatus.NOT_ACCEPTABLE, null, "当前禁止注册"))
            }
        } catch (e: Exception) {
            ackSender.sendAckData(HandUtils.printErrorLog(e))
        }
    }

    fun handleUserScanLogin(client: SocketIOClient, ackSender: AckRequest) {
        try {
            if (!onlineEvent.checkClient(HandUtils.encodeStringToMD5(client.sessionId.toString()))) {
                ackSender.sendAckData(HandUtils.handleResultByCode(HttpStatus.FORBIDDEN, null, "禁止访问"))
            } else {
                val qid = IDGenerator.generateUniqueId()
                val content = mapOf(
                    "qid" to qid,
                    "type" to "login",
                    "platform" to "H5"
                )
                tokenService.setScanStatus(qid, 0)
                /*QrcodeUtils.generateQrcode(content.toString())*/
                ackSender.sendAckData(HandUtils.handleResultByCode(HttpStatus.OK, content, "获取成功"))
            }
        } catch (e: Exception) {
            ackSender.sendAckData(HandUtils.printErrorLog(e))
        }
    }

    fun handleGetScanLoginStatus(client: SocketIOClient, data: Map<String?, Any>, ackSender: AckRequest) {
        try {
            if (!onlineEvent.checkClient(HandUtils.encodeStringToMD5(client.sessionId.toString()))) {
                ackSender.sendAckData(HandUtils.handleResultByCode(HttpStatus.FORBIDDEN, null, "禁止访问"))
            } else {
                val qid = data["qid"]?.toString()
                ackSender.sendAckData(
                    if (qid.isNullOrEmpty())
                        HandUtils.printParamError()
                    else clientUserService.getUserQrcodeScanStatus(
                        qid = qid,
                        address = clientService.getRemoteAddress(client)
                    )
                )
            }
        } catch (e: Exception) {
            ackSender.sendAckData(HandUtils.printErrorLog(e))
        }
    }
}