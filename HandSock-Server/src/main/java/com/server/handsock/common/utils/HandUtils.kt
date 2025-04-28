package com.server.handsock.common.utils

import com.corundumstudio.socketio.SocketIOClient
import com.corundumstudio.socketio.SocketIOServer
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.server.handsock.common.data.ResultModel
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.web.multipart.MultipartFile
import java.security.MessageDigest
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.regex.Pattern
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import javax.imageio.ImageIO

object HandUtils {
    private const val USERNAME_PATTERN = "^[a-zA-Z0-9_-]{3,16}$"
    private var SECRET_KEY = GlobalService.handsockProps?.secretKey
    private const val PASSWORD_PATTERN = "^[a-zA-Z0-9_@#$%^&*!-]{6,18}$"

    fun printParamError(): Map<String, Any> {
        return handleResultByCode(HttpStatus.NOT_ACCEPTABLE, null, "参数有错误")
    }

    fun printErrorLog(e: Exception): Map<String, Any> {
        ConsoleUtils.printErrorLog(e)
        return handleResultByCode(HttpStatus.INTERNAL_SERVER_ERROR, null, "服务端异常")
    }

    fun isValidUsername(username: String): Boolean {
        return !username.matches(USERNAME_PATTERN.toRegex()) || username.length < 5 || username.length > 20
    }

    fun isValidPassword(password: String): Boolean {
        return !password.matches(PASSWORD_PATTERN.toRegex()) || password.length < 6 || password.length > 20
    }

    fun stripHtmlTagsForString(html: String): String {
        return Pattern.compile("<.*?>", Pattern.DOTALL).matcher(html).replaceAll("")
    }

    fun formatBytesForString(bytes: Long): String {
        return if (bytes < 1024) {
            bytes.toString() + "B"
        } else if (bytes < 1024 * 1024) {
            String.format("%.1fKB", bytes / 1024.0)
        } else if (bytes < 1024L * 1024L * 1024L) {
            String.format("%.1fMB", bytes / (1024.0 * 1024))
        } else String.format("%.1fGB", bytes / (1024.0 * 1024 * 1024))
    }

    fun handleResultByCode(status: HttpStatus, data: Any?, message: String): Map<String, Any> {
        val result = ResultModel(
            data = data,
            code = status.value(),
            message = message
        )
        return objectMapper.convertValue(result, object : TypeReference<Map<String, Any>>() {})
    }

    fun formatTimeForString(pattern: String?): String {
        val now = OffsetDateTime.now()
        val formatter = pattern?.let { DateTimeFormatter.ofPattern(it) }
        return now.format(formatter)
    }

    fun sendGlobalMessage(server: SocketIOServer, event: String, content: Any?) {
        val broadcastOperations = server.broadcastOperations
        broadcastOperations.sendEvent(event, content)
    }

    fun sendRoomMessage(server: SocketIOServer, client: SocketIOClient, event: String, content: Any?) {
        @Suppress("UNCHECKED_CAST")
        val headers = client.handshakeData.authToken as Map<String, Any>
        sendRoomMessageApi(server, headers["gid"].toString(), event, content)
    }

    fun sendRoomMessageApi(server: SocketIOServer, gid: String, event: String, content: Any?) {
        val broadcastOperations = server.getRoomOperations(gid)
        broadcastOperations.sendEvent(event, content)
    }

    fun checkImageValidExtension(file: MultipartFile): Boolean {
        val fileName = checkNotNull(file.originalFilename)
        val isValidExtension = fileName.endsWith(".jpg")
                || fileName.endsWith(".gif")
                || fileName.endsWith(".png")
                || fileName.endsWith(".jpeg")
                || fileName.endsWith(".webp")
        if (!isValidExtension) return false
        try {
            ImageIO.read(file.inputStream) ?: return false
        } catch (e: Exception) {
            ConsoleUtils.printErrorLog(e)
            return false
        }
        return true
    }

    fun encodeStringToMD5(text: String): String {
        val md = MessageDigest.getInstance("MD5")
        val messageDigest = md.digest(text.toByteArray())
        val hexString = StringBuilder()
        for (b in messageDigest) {
            val hex = Integer.toHexString(0xff and b.toInt())
            if (hex.length == 1) hexString.append('0')
            hexString.append(hex)
        }
        return hexString.toString()
    }

    fun encryptString(input: String): String {
        val secretKey = SecretKeySpec(SECRET_KEY?.toByteArray(), "AES")
        val cipher = Cipher.getInstance("AES")
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        val encryptedBytes = cipher.doFinal(input.toByteArray())
        return Base64.getEncoder().encodeToString(encryptedBytes)
    }

    fun decryptString(encrypted: String): String {
        val secretKey = SecretKeySpec(SECRET_KEY?.toByteArray(), "AES")
        val cipher = Cipher.getInstance("AES")
        cipher.init(Cipher.DECRYPT_MODE, secretKey)
        val decodedBytes = Base64.getDecoder().decode(encrypted)
        val decryptedBytes = cipher.doFinal(decodedBytes)
        return String(decryptedBytes)
    }

    fun getHttpClientIp(request: HttpServletRequest): String {
        var ip: String? = request.getHeader("X-Forwarded-For")
        if (ip.isNullOrBlank() || "unknown".equals(ip, ignoreCase = true)) {
            ip = request.getHeader("Proxy-Client-IP")
        }
        if (ip.isNullOrBlank() || "unknown".equals(ip, ignoreCase = true)) {
            ip = request.getHeader("WL-Proxy-Client-IP")
        }
        if (ip.isNullOrBlank() || "unknown".equals(ip, ignoreCase = true)) {
            ip = request.getHeader("HTTP_CLIENT_IP")
        }
        if (ip.isNullOrBlank() || "unknown".equals(ip, ignoreCase = true)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR")
        }
        if (ip.isNullOrBlank() || "unknown".equals(ip, ignoreCase = true)) {
            ip = request.remoteAddr
        }
        return ip?.split(",")?.firstOrNull()?.trim() ?: "unknown"
    }

    private val objectMapper = ObjectMapper().apply {
        registerModule(JavaTimeModule())
        setSerializationInclusion(JsonInclude.Include.NON_NULL)
        disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    }
}