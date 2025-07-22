package top.chengdongqing.weui.core.utils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun isValidUrl(input: String): Boolean {
    val urlPattern = "^(https?|ftp)://[^\\s/$.?#].\\S*$".toRegex()
    return urlPattern.matches(input)
}

fun String.filterNumber(): String {
    return replace(Regex("[^0-9]"), "")
}

fun String.filterAccount(): String {
    return replace(Regex("[^A-Za-z0-9_]"), "")
}

fun String.filterPassword(): String {
    return replace(Regex("[^A-Za-z0-9!@#$%^&*()_+\\-={}\\\\|;:'\",.<>/?~]"), "")
}

fun getDateByDateTime(dateTime: String): String {
    val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    val outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    try {
        val localDateTime = LocalDateTime.parse(dateTime, inputFormatter)
        return localDateTime.format(outputFormatter)
    } catch (e: Exception) {
        throw IllegalArgumentException("Invalid date-time format. Please use 'yyyy-MM-dd HH:mm:ss'.", e)
    }
}
