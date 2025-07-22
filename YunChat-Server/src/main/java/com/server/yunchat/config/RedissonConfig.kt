package com.server.yunchat.config

import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.config.Config
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class RedissonConfig {
    @Value("\${spring.data.redis.url}")
    private val redisUrl: String? = null

    @Bean(destroyMethod = "shutdown")
    open fun redissonClient(): RedissonClient {
        val config = Config()
        config.useSingleServer()
            .setAddress(redisUrl)
            .setConnectionPoolSize(64)
        return Redisson.create(config)
    }
}