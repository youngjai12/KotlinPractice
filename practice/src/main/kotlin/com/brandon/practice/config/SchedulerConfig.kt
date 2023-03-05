package com.brandon.practice.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.SchedulingConfigurer
import org.springframework.scheduling.config.ScheduledTaskRegistrar
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService

@Configuration
@EnableScheduling
class SchedulerConfig : SchedulingConfigurer {

    companion object{
        val POOL_SIZE = 5
    }

    @Bean(name = ["priceMonitorScheduler"])
    fun priceMonitorScheduler(): ScheduledExecutorService = Executors.newScheduledThreadPool(POOL_SIZE)

    @Bean(name = ["queueExecuteScheduler"])
    fun queExecuteScheduler(): ScheduledExecutorService = Executors.newScheduledThreadPool(2)

    override fun configureTasks(taskRegistrar: ScheduledTaskRegistrar) {
        taskRegistrar.setScheduler(priceMonitorScheduler())
        taskRegistrar.setScheduler(queExecuteScheduler())
    }

}
