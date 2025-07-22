package com.android.yunchat.service.impl

import android.util.Base64
import com.android.yunchat.config.AppConfig
import com.android.yunchat.service.EncryptService
import com.android.yunchat.service.instance.userServiceInstance
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * @name 加密服务实现类
 * @author yichen9247
 */
class EncryptServiceImpl: EncryptService {
    /**
     * @name 加密文本
     * @param plainText 待加密的文本
     * @returns Promise<string> Base64格式的加密字符串
     */
    private fun encryptText(plainText: String): String {
        val keySpec = SecretKeySpec(SECRET_KEY.toByteArray(Charsets.UTF_8), ALGORITHM)
        val ivSpec = IvParameterSpec(SECRET_KEY.substring(0, 16).toByteArray(Charsets.UTF_8))
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec)
        val encryptedBytes = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))
        return Base64.encodeToString(encryptedBytes, Base64.NO_WRAP)
    }

    /**
     * @name 加密授权信息
     * @returns Promise<string> Base64格式的加密字符串
     */
    override fun encryptAuthorization(): String {
        val uid = userServiceInstance.getUserUid()
        val token = userServiceInstance.getUserToken()
        val timestamp = System.currentTimeMillis()
        val plainText = "$uid||$token||App||$timestamp"
        return encryptText(plainText)
    }

    /**
     * @name 加密登录信息
     * @param username 账号
     * @param password 密码
     * @returns Promise<string> Base64格式的加密字符串
     */
    override fun encryptLogin(username: String, password: String): String {
        val timestamp = System.currentTimeMillis()
        val plainText = "$username||$password||App||$timestamp"
        return encryptText(plainText)
    }

    companion object {
        private const val ALGORITHM = "AES"
        private const val SECRET_KEY = AppConfig.secretKey
        private const val TRANSFORMATION = "AES/CBC/PKCS7Padding"
    }
}