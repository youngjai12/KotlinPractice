package com.brandon.practice.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.SchedulingConfigurer
import org.springframework.scheduling.config.ScheduledTaskRegistrar
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService

@Configuration
@EnableScheduling
class SchedulerConfig : SchedulingConfigurer {

    companion object{
        val POOL_SIZE = 2
    }

    @Bean
    fun priceCheckScheduler(): ScheduledExecutorService = Executors.newScheduledThreadPool(POOL_SIZE)

    override fun configureTasks(taskRegistrar: ScheduledTaskRegistrar) {
        taskRegistrar.setScheduler(priceCheckScheduler())
    }

}
