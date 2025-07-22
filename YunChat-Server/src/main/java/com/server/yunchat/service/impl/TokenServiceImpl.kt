package com.server.yunchat.service.impl

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.JWTVerifier
import com.server.yunchat.builder.data.RedisUserModel
import com.server.yunchat.builder.props.YunChatProps
import com.server.yunchat.builder.types.RedisType.USERS_TOKEN_KEY
import com.server.yunchat.builder.utils.GlobalService
import com.server.yunchat.builder.utils.HandUtils
import com.server.yunchat.service.TokenService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.util.*

/**
 * @name 密钥服务实现类
 * @author yichen9247
 */
@Service
class TokenServiceImpl @Autowired constructor(
    private val yunChatProps: YunChatProps,
    @Qualifier("onlineUserRedisTemplate")
    private val onlineUserRedisTemplate: RedisTemplate<String, RedisUserModel>
): TokenService {

    /**
     * @name 设置用户密钥
     * @return String
     */
    override fun setUserToken(uid: Long, username: String): String {
        val uidKey = uid.toString()
        val userRecord = onlineUserRedisTemplate.opsForHash<String, RedisUserModel>().get(
            USERS_TOKEN_KEY, uidKey
        )
        userRecord?.token?.takeIf { it.isNotBlank() }?.let {
            return HandUtils.encryptString(it)
        }
        val newToken = createUserToken(uid, username).also { token ->
            onlineUserRedisTemplate.opsForHash<String, RedisUserModel>().apply {
                put(USERS_TOKEN_KEY, uidKey, RedisUserModel(token = token))
            }
        }
        return HandUtils.encryptString(newToken)
    }

    /**
     * @name 验证密钥状态
     * @return Boolean
     */
    override fun getUserTokenStatus(uid: Long, token: String): Boolean {
        return HandUtils.decryptString(token).takeIf {
            verifyUserToken(it)
        }?.let { decryptedToken ->
            onlineUserRedisTemplate.opsForHash<String, RedisUserModel>().get(
                USERS_TOKEN_KEY, uid.toString()
            )?.token == decryptedToken
        } ?: false
    }

    /**
     * @name 验证用户密钥
     * @return Boolean
     */
    override fun verifyUserToken(token: String): Boolean {
        val secretKey = yunChatProps.secretKey?.toByteArray()
        try {
            val algorithm = Algorithm.HMAC256(secretKey)
            val verifier: JWTVerifier = JWT.require(algorithm).build()
            verifier.verify(token)
            return true
        } catch (e: Exception) {
            return false
        }
    }

    /**
     * @name 生成用户密钥
     * @return String
     */
    override fun createUserToken(uid: Long, username: String): String {
        val secretKey = GlobalService.yunchatProps?.secretKey?.toByteArray()
        val expirationDate = Date(Date().time + 30L * 24 * 60 * 60 * 1000)
        return JWT.create().withClaim("uid", uid).withClaim("username", username).withExpiresAt(expirationDate).sign(Algorithm.HMAC256(secretKey))
    }
}