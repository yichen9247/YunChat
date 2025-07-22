package com.server.yunchat.builder.utils

import com.server.yunchat.builder.data.ResultModel
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
    private var SECRET_KEY = GlobalService.yunchatProps?.secretKey

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

    fun handleResultByCode(status: HttpStatus, data: Any?, message: String): ResultModel {
        return ResultModel(
            data = data,
            message = message,
            code = status.value()
        )
    }

    fun formatTimeForString(pattern: String?): String {
        val now = OffsetDateTime.now()
        val formatter = pattern?.let { DateTimeFormatter.ofPattern(it) }
        return now.format(formatter)
    }

    fun checkVideoValidExtension(file: MultipartFile): Boolean {
        val fileName = file.originalFilename ?: return false
        val extension = fileName.substringAfterLast('.', "").lowercase()
        if (extension != "mp4") return false
        return try {
            file.inputStream.use { input ->
                val buffer = ByteArray(8)
                if (input.read(buffer) != 8) return false
                val header = String(buffer, 4, 4, Charsets.US_ASCII)
                header == "ftyp"
            }
        } catch (e: Exception) {
            ConsoleUtils.printErrorLog(e)
            false
        }
    }

    fun checkImageValidExtension(file: MultipartFile): Boolean {
        val fileName = checkNotNull(file.originalFilename)
        val extension = fileName.substringAfterLast('.', "").lowercase()
        val isValidExtension = extension == "jpg" || extension == "jpeg" || extension == "png" || extension == "gif" || extension == "webp"
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
}