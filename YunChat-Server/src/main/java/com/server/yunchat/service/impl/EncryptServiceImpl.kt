package com.server.yunchat.service.impl

import com.server.yunchat.builder.data.UserAuthorizationModel
import com.server.yunchat.builder.data.UserDecryptLoginModel
import com.server.yunchat.builder.props.YunChatProps
import com.server.yunchat.service.EncryptService
import org.apache.commons.codec.binary.Base64
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.Instant
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlin.math.abs

/**
 * @name 加密服务实现类
 * @author yichen9247
 */
@Service
class EncryptServiceImpl @Autowired constructor(
    private val yunChatProps: YunChatProps
) : EncryptService {
    /**
     * @name 解密授权信息
     * @param authHeader 授权头
     * @param validTimestamp 是否验证时间戳
     * @return UserAuthorizationModel?
     */
    override fun decryptAuthorization(
        authHeader: String,
        validTimestamp: Boolean
    ): UserAuthorizationModel? {
        val parts = decryptAndSplit(authHeader.removePrefix("Bearer").trim()) ?: return null
        return try {
            val timestamp = parts[3].toLong()
            UserAuthorizationModel(
                uid = parts[0].toLong(),
                token = parts[1],
                platform = parts[2],
                timestamp = timestamp
            )
        } catch (e: Exception) {
            null
        }
    }

    /**
     * @name 解密登录信息
     * @param data 登录信息
     * @return UserDecryptLoginModel?
     */
    override fun decryptLogin(data: String): UserDecryptLoginModel? {
        val parts = decryptAndSplit(data) ?: return null
        return try {
            val timestamp = parts[3].toLong()
            if (!validateTimestamp(timestamp)) {
                throw SecurityException("Timestamp validation failed")
            }
            UserDecryptLoginModel(
                username = parts[0],
                password = parts[1],
                platform = parts[2],
                timestamp = timestamp
            )
        } catch (e: Exception) {
            null
        }
    }

    /**
     * @name 解密并分割字符串
     * @param encryptedData 加密数据
     * @return List<String>?
     */
    private fun decryptAndSplit(encryptedData: String): List<String>? {
        return try {
            val decodedData = Base64.decodeBase64(encryptedData)
            val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
            val keySpec = SecretKeySpec(yunChatProps.secretKey.toByteArray(Charsets.UTF_8), "AES")
            val ivSpec = IvParameterSpec(yunChatProps.secretKey.toByteArray(Charsets.UTF_8))
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec)
            val decryptedBytes = cipher.doFinal(decodedData)
            val decryptedText = String(decryptedBytes, Charsets.UTF_8)
            decryptedText.split(CHAR_PART).takeIf { it.size == EXPECTED_PARTS }
        } catch (ex: Exception) {
            null
        }
    }

    /**
     * @name 验证时间戳
     * @param timestamp 时间戳
     * @return Boolean
     */
    private fun validateTimestamp(timestamp: Long): Boolean {
        val serverTime = Instant.now().toEpochMilli()
        val timeDiff = abs(serverTime - timestamp)
        return timeDiff <= TIME_TOLERANCE
    }

    companion object {
        private const val CHAR_PART = "||"
        private const val EXPECTED_PARTS = 4
        private const val TIME_TOLERANCE: Long = 60 * 1000
    }
}