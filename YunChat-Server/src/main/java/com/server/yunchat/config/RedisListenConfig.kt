package com.server.yunchat.config

import com.server.yunchat.builder.types.RedisType.ONLINE_USERS_KEY
import com.server.yunchat.builder.types.RedisType.ROBOT_MESSAGE_KEY
import com.server.yunchat.listener.OnlineListener
import com.server.yunchat.listener.RobotListener
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.listener.ChannelTopic
import org.springframework.data.redis.listener.RedisMessageListenerContainer
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter

@Configuration
open class RedisListenConfig(
    private val robotListener: RobotListener,
    private val onlineListener: OnlineListener
) {
    @Bean
    open fun container(redisConnectionFactory: RedisConnectionFactory): RedisMessageListenerContainer {
        val container = RedisMessageListenerContainer()
        container.connectionFactory = redisConnectionFactory

        // 在线成员广播
        container.addMessageListener(
            MessageListenerAdapter(onlineListener),
            ChannelTopic(ONLINE_USERS_KEY)
        )

        // 机器人公共广播
        container.addMessageListener(
            MessageListenerAdapter(robotListener),
            ChannelTopic(ROBOT_MESSAGE_KEY)
        )
        return container
    }
}