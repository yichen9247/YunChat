package com.server.yunchat.builder.service

import com.corundumstudio.socketio.SocketIOClient
import com.server.yunchat.builder.data.UserAuthorizationModel
import com.server.yunchat.builder.props.YunChatProps
import com.server.yunchat.builder.types.UserAuthType
import com.server.yunchat.service.impl.*
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class AuthObjService @Autowired constructor(
    private val yunChatProps: YunChatProps,
    private val userServiceImpl: UserServiceImpl,
    private val tokenServiceImpl: TokenServiceImpl,
    private val redisServiceImpl: RedisServiceImpl,
    private val clientServiceImpl: ClientServiceImpl,
    private val encryptServiceImpl: EncryptServiceImpl
) {
    fun getUserAuthInfo(uid: Long): Int {
        userServiceImpl.getClientUserInfo(uid)?.let {
            return it.permission
        }.also {
            return UserAuthType.VISITOR_AUTHENTICATION
        }
    }

    fun validClientTokenBySocket(client: SocketIOClient): UserAuthorizationModel? {
        return try {
            @Suppress("UNCHECKED_CAST")
            val authToken = client.handshakeData.authToken as? Map<String, Any> ?: return null
            val authorization = authToken["authorization"]?.toString() ?: return null
            validateAuthorization(authorization, validTimestamp = false)
        } catch (e: Exception) {
            null
        }
    }

    fun validClientTokenByRequest(request: HttpServletRequest): Boolean {
        return try {
            val authorization = request.getHeader("authorization") ?: ""
            validateAuthorization(authorization, validTimestamp = true) != null
        } catch (e: Exception) {
            false
        }
    }

    fun validApiRequestTime(request: HttpServletRequest): Boolean {
        val address = clientServiceImpl.getHttpClientIp(request)
        if (redisServiceImpl.getOpenApiCache(address)) {
            redisServiceImpl.setOpenApiCache(address)
            return true
        } else return false
    }

    fun validOpenApiRequestLimit(request: HttpServletRequest): Boolean {
        val authorization = request.getHeader("Authorization") ?: null
        return !(authorization == null || authorization != yunChatProps.openapi)
    }

    private fun validateAuthorization(authString: String, validTimestamp: Boolean): UserAuthorizationModel? {
        return encryptServiceImpl.decryptAuthorization(authString, validTimestamp)?.takeIf {
            tokenServiceImpl.getUserTokenStatus(it.uid, it.token)
        }
    }
}