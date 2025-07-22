package com.server.yunchat.listener

import com.server.yunchat.service.impl.RobotServiceImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.connection.Message
import org.springframework.data.redis.connection.MessageListener
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets

@Component
class RobotListener @Autowired constructor(
    private val robotServiceImpl: RobotServiceImpl
): MessageListener {
    override fun onMessage(message: Message, pattern: ByteArray?) {
        var content = String(message.body, StandardCharsets.UTF_8)
        if (content.startsWith('"') && content.endsWith('"')) {
            content = content.substring(1, content.length - 1)
        }
        robotServiceImpl.sendRobotMessage(content, 0, 1, "text")
    }
}