package com.server.yunchat.builder.utils

import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.util.*
import javax.imageio.ImageIO

/**
 * @name 二维码工具类
 * @author yichen9247
 * 已弃用，由前端生成二维码
 */
object QrcodeUtils {
    fun generateQrcode(content: String): String {
        val writer = QRCodeWriter()
        val hints = HashMap<EncodeHintType, Any>()
        hints[EncodeHintType.MARGIN] = 0
        hints[EncodeHintType.CHARACTER_SET] = "UTF-8"
        hints[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.H
        val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, 200, 200, hints)
        return base64ToDataURL(imageToBase64(toBufferedImage(bitMatrix)))
    }

    private fun toBufferedImage(bitMatrix: BitMatrix): BufferedImage {
        val width = bitMatrix.width
        val height = bitMatrix.height
        val image = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)

        for (x in 0 until width) {
            for (y in 0 until height) {
                image.setRGB(x, y, if (bitMatrix[x, y]) 0xFF000000.toInt() else 0xFFFFFFFF.toInt())
            }
        }
        return image
    }

    private fun imageToBase64(image: BufferedImage): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        ImageIO.write(image, "png", byteArrayOutputStream)
        val imageBytes = byteArrayOutputStream.toByteArray()
        return Base64.getEncoder().encodeToString(imageBytes)
    }

    private fun base64ToDataURL(base64String: String): String {
        return "data:image/png;base64,${base64String}"
    }
}