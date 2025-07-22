package com.server.yunchat.config

import com.server.yunchat.builder.data.RedisUserModel
import com.server.yunchat.builder.model.GroupModel
import com.server.yunchat.client.mod.ClientUserModel
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
open class RedisConfig {

    @Bean
    open fun defaultRedisTemplate(
        redisConnectionFactory: RedisConnectionFactory
    ): RedisTemplate<String, String> = RedisTemplate<String, String>().apply {
        connectionFactory = redisConnectionFactory
        val stringSerializer = StringRedisSerializer()
        keySerializer = stringSerializer
        valueSerializer = stringSerializer
        hashKeySerializer = stringSerializer
        hashValueSerializer = stringSerializer
        afterPropertiesSet()
    }

    private inline fun <reified T> createJsonRedisTemplate(
        redisConnectionFactory: RedisConnectionFactory
    ): RedisTemplate<String, T> = RedisTemplate<String, T>().apply {
        val jsonSerializer = GenericJackson2JsonRedisSerializer()
        valueSerializer = jsonSerializer
        hashValueSerializer = jsonSerializer
        keySerializer = StringRedisSerializer()
        connectionFactory = redisConnectionFactory
        hashKeySerializer = StringRedisSerializer()
        afterPropertiesSet()
    }

    @Bean
    open fun onlineUserRedisTemplate(
        redisConnectionFactory: RedisConnectionFactory
    ): RedisTemplate<String, RedisUserModel> = createJsonRedisTemplate(redisConnectionFactory)

    @Bean
    open fun userInfoRedisTemplate(
        redisConnectionFactory: RedisConnectionFactory
    ): RedisTemplate<String, ClientUserModel> = createJsonRedisTemplate(redisConnectionFactory)

    @Bean
    open fun groupInfoRedisTemplate(
        redisConnectionFactory: RedisConnectionFactory
    ): RedisTemplate<String, GroupModel> = createJsonRedisTemplate(redisConnectionFactory)
}