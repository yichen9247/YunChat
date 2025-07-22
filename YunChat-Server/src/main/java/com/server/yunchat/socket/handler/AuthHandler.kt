package com.server.yunchat.socket.handler

import com.corundumstudio.socketio.SocketIOClient
import com.server.yunchat.builder.data.ResultModel
import com.server.yunchat.builder.data.SocketScanStatus
import com.server.yunchat.builder.data.SocketUserLogin
import com.server.yunchat.builder.data.UserDecryptLoginModel
import com.server.yunchat.builder.utils.HandUtils
import com.server.yunchat.builder.utils.ResultUtils
import com.server.yunchat.client.service.ClientUserService
import com.server.yunchat.service.impl.ClientServiceImpl
import com.server.yunchat.service.impl.EncryptServiceImpl
import com.server.yunchat.service.impl.OnlineServiceImpl
import com.server.yunchat.service.impl.RedisServiceImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import java.util.*

@Service
class AuthHandler @Autowired constructor(
    private val redisServiceImpl: RedisServiceImpl,
    private val onlineServiceImpl: OnlineServiceImpl,
    private val clientServiceImpl: ClientServiceImpl,
    private val clientUserService: ClientUserService,
    private val encryptServiceImpl: EncryptServiceImpl
) {
    // 用户登录
    fun handleUserLogin(client: SocketIOClient, data: SocketUserLogin): ResultModel {
        val loginData = validateRequest(client, data) ?: return handleValidationFailure()
        return clientUserService.loginUser(
            platform = loginData.platform,
            username = loginData.username,
            password = loginData.password,
            address = clientServiceImpl.getSocketClientIp(client)
        )
    }

    // 用户注册
    fun handleUserRegister(client: SocketIOClient, data: SocketUserLogin): ResultModel {
        val loginData = validateRequest(client, data) ?: return handleValidationFailure()
        return clientUserService.registerUser(
            platform = loginData.platform,
            username = loginData.username,
            password = loginData.password,
            address = clientServiceImpl.getSocketClientIp(client)
        )
    }

    // 扫码登录
    fun handleUserScanLogin(client: SocketIOClient): ResultModel {
        val qid = UUID.randomUUID().toString()
        val content = mapOf(
            "qid" to qid,
            "type" to "login",
            "platform" to "Web",
            "address" to clientServiceImpl.getSocketClientIp(client)
        )
        redisServiceImpl.setUserScanStatus(qid, 0)
        return HandUtils.handleResultByCode(HttpStatus.OK, content, "获取成功")
    }

    // 获取扫码登录状态
    fun handleGetScanLoginStatus(client: SocketIOClient, data: SocketScanStatus): ResultModel {
        return if (data.tempToken?.let { onlineServiceImpl.getClientOnlineStatus(client, it) } == true) {
            val qid = data.qid
            val address = clientServiceImpl.getSocketClientIp(client)
            if (qid.isNullOrEmpty()) {
                ResultUtils.printParamMessage()
            } else clientUserService.getUserQrcodeScanStatus(qid, address)
        } else HandUtils.handleResultByCode(HttpStatus.FORBIDDEN, null, "禁止访问")
    }

    private fun validateRequest(client: SocketIOClient, data: SocketUserLogin): UserDecryptLoginModel? {
        val isTokenValid = data.tempToken?.let { token ->
            onlineServiceImpl.getClientOnlineStatus(client, token)
        } ?: false
        if (!isTokenValid) return null
        val encryptedData = data.data
        if (encryptedData.isNullOrEmpty()) return null
        return encryptServiceImpl.decryptLogin(encryptedData)
    }

    private fun handleValidationFailure(): ResultModel {
        return HandUtils.handleResultByCode(HttpStatus.FORBIDDEN, null, "禁止访问")
    }
}