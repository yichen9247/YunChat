package com.server.yunchat

import com.server.yunchat.builder.utils.ConsoleUtils
import com.server.yunchat.scripts.ServerChecker
import org.mybatis.spring.annotation.MapperScan
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.scheduling.annotation.EnableScheduling
import kotlin.system.exitProcess

@EnableScheduling
@SpringBootApplication
@MapperScan("com.server.yunchat")
open class YunChat(
    private val serverChecker: ServerChecker
) {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            try {
                System.setProperty("spring.devtools.restart.enabled", "false")
                val context = SpringApplication.run(YunChat::class.java, *args)
                ConsoleUtils.printSuccessLog("Springboot server started!")
                context.getBean(YunChat::class.java).serverChecker.checkMysqlConnection()
                context.getBean(YunChat::class.java).serverChecker.checkRedisConnection()
                context.getBean(YunChat::class.java).serverChecker.checkDatabaseTable()
            } catch (e: Exception) {
                ConsoleUtils.printErrorLog(e)
                exitProcess(0)
            }
        }
    }
}