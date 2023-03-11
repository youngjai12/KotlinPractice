package com.brandon.practice.threadExp

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor

@Configuration
@EnableAsync
class PoolExecutorConfig {

    @Bean
    fun testPoolExecutor(): ThreadPoolTaskExecutor {
        val executor = ThreadPoolTaskExecutor()
        executor.corePoolSize = 2
        executor.maxPoolSize = 10
        executor.setThreadNamePrefix("BrandonTest-")
        executor.initialize()

        return executor
    }
}
