package com.server.yunchat.listener

import com.server.yunchat.builder.utils.HandUtils
import com.server.yunchat.service.impl.OnlineServiceImpl
import com.server.yunchat.service.impl.SocketServiceImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.connection.Message
import org.springframework.data.redis.connection.MessageListener
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component

@Component
class OnlineListener @Autowired constructor(
    private val onlineServiceImpl: OnlineServiceImpl,
    private val socketServiceImpl: SocketServiceImpl
): MessageListener {
    override fun onMessage(message: Message, pattern: ByteArray?) {
        socketServiceImpl.sendGlobalMessage("[CLIENT:ONLINE]", HandUtils.handleResultByCode(
            HttpStatus.OK, onlineServiceImpl.getOnlineUserList(), message.toString()
        ))
    }
}