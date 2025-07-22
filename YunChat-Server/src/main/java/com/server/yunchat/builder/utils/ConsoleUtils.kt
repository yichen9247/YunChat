package com.server.yunchat.builder.utils

import com.server.yunchat.service.impl.ClientServiceImpl
import jakarta.servlet.http.HttpServletRequest
import lombok.extern.slf4j.Slf4j
import org.slf4j.LoggerFactory

/**
 * @name 日志工具类
 * @author yichen9247
 */
@Slf4j
object ConsoleUtils {
    private val clientServiceImpl = ClientServiceImpl()
    private val logger = LoggerFactory.getLogger("YunChat-Server")

    fun printErrorLog(content: Any) {
        logger.error(content.toString())
    }

    fun printInfoLog(content: Any) {
        logger.info(content.toString())
    }

    fun printWarnLog(content: Any) {
        logger.warn(content.toString())
    }

    fun printSuccessLog(content: Any) {
        logger.info(content.toString())
    }

    fun printCustomLogs(
        uid: Long = 0,
        address: String,
        content: String,
        hash: String = "无",
        message: String = "无",
    ) {
        printInfoLog(
            "事件：$content - 地址: $address - 用户: $uid - 哈希: $hash - 详细: $message"
        )
    }

    fun printCustomException(
        content: String,
        message: String,
        request: HttpServletRequest
    ) {
        printInfoLog(
            "错误：$content - 地址: ${clientServiceImpl.getHttpClientIp(request)} - 方式: ${request.method} - 路径: ${request.requestURI} - 详细: $message"
        )
    }
}