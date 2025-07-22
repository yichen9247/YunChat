package com.server.yunchat.plugin

import com.server.yunchat.builder.model.GroupModel
import com.server.yunchat.builder.types.RedisType.GROUP_INFO_KEY
import com.server.yunchat.builder.types.RedisType.USER_INFO_KEY
import com.server.yunchat.builder.utils.ConsoleUtils
import com.server.yunchat.client.mod.ClientUserModel
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class TaskPlugin @Autowired constructor(
    @Qualifier("groupInfoRedisTemplate")
    private val groupInfoRedisTemplate: RedisTemplate<String, GroupModel>,
    @Qualifier("userInfoRedisTemplate")
    private val userInfoRedisTemplate: RedisTemplate<String, ClientUserModel>
) {
    @PostConstruct // 初始化时执行一次
    @Scheduled(cron = "0 */30 * * * ?") // 每30分钟清理一次缓存
    fun deleteRedisCache() {
        doDeleteRedisCache()
        ConsoleUtils.printCustomLogs(
            address = "0.0.0.0",
            content = "Redis cache deleted",
            message = System.currentTimeMillis().toString()
        )
    }

    fun doDeleteRedisCache() {
        userInfoRedisTemplate.delete(USER_INFO_KEY)
        groupInfoRedisTemplate.delete(GROUP_INFO_KEY)
    }
}