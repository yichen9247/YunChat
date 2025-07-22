package com.server.yunchat.scripts

import com.server.yunchat.builder.utils.ConsoleUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.connection.RedisConnection
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component
import kotlin.system.exitProcess

@Component
class ServerChecker @Autowired constructor(
    private val jdbcTemplate: JdbcTemplate,
    private val mysqlChecker: MysqlChecker,
    private val redisTemplate: StringRedisTemplate
) {

    @Value("\${spring.datasource.url}")
    private val databaseUrl: String? = null

    @Value("\${spring.datasource.username}")
    private val databaseUsername: String? = null

    @Value("\${spring.datasource.password}")
    private val databasePassword: String? = null

    @Value("\${spring.datasource.driver-class-name}")
    private val databaseDriver: String? = null

    fun checkMysqlConnection() {
        try {
            val result = jdbcTemplate.queryForList("SELECT 1")
            if (result.isEmpty()) shutdown(Throwable("Redis server connect failed!"))
        } catch (e: Exception) {
            ConsoleUtils.printErrorLog(e)
            shutdown(Throwable("Redis server connect failed!"))
        }
    }

    fun checkRedisConnection() {
        try {
            val pingResult = redisTemplate.execute { obj: RedisConnection -> obj.ping() }
            if (!"PONG".equals(pingResult, ignoreCase = true)) shutdown(Throwable("Mysql server connect failed!"))
        } catch (e: Exception) {
            ConsoleUtils.printErrorLog(e)
            shutdown(Throwable("Redis server connect failed!"))
        }
    }

    fun checkDatabaseTable() {
        try {
            mysqlChecker.checkTable(databaseDriver, databaseUrl, databaseUsername, databasePassword)
        } catch (e: Exception) {
            ConsoleUtils.printErrorLog(e)
            shutdown(Throwable("Database table checked failed!"))
        }
    }

    companion object {
        private fun shutdown(content: Throwable) {
            ConsoleUtils.printErrorLog(content)
            exitProcess(0)
        }
    }
}