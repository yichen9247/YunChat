package com.android.yunchat.utils

import androidx.core.text.HtmlCompat

/**
 * @name 字符串工具类
 * @author yichen9247
 */
object StringUtils {

    // 将字符串转换为Html格式
    fun getHtmlContent(content: String): String {
        return HtmlCompat.fromHtml(content, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
    }

    // 获取指定格式消息后的时间
    fun getMessageTime(content: String): String {
        return content.split(" ")[1]
    }
}