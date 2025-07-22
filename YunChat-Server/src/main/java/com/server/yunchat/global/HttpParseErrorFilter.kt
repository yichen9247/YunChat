package com.server.yunchat.global

import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.classic.spi.ThrowableProxy
import ch.qos.logback.core.filter.Filter
import ch.qos.logback.core.spi.FilterReply

class HttpParseErrorFilter : Filter<ILoggingEvent>() {
    // 过滤Spring框架外的所有异常
    override fun decide(event: ILoggingEvent): FilterReply {
        if (event.throwableProxy != null && event.throwableProxy is ThrowableProxy) {
            val throwableProxy = event.throwableProxy as ThrowableProxy
            val message = throwableProxy.message ?: ""
            println("[ERROR-ALERT]检测到异常: $message")
            return FilterReply.DENY
        }
        return FilterReply.NEUTRAL
    }
}