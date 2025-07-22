package com.server.yunchat.config

import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.asExecutor
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.AsyncConfigurer
import org.springframework.scheduling.annotation.EnableAsync
import java.util.concurrent.Executor
import java.util.concurrent.Executors

@Configuration
@EnableAsync
open class AsyncConfig : AsyncConfigurer {
    override fun getAsyncExecutor(): Executor {
        return Executors.newFixedThreadPool(10).asCoroutineDispatcher().asExecutor()
    }
}