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
class ScheduledConfig: SchedulingConfigurer {

    val PRICE_MONITOR_POOL_SIZE = 6
    val QUE_EXEUTE_POOL_SIZE = 3


    @Bean(name = ["priceMonitorScheduler"])
    fun priceMonitorScheduler(): ScheduledExecutorService = Executors.newScheduledThreadPool(PRICE_MONITOR_POOL_SIZE)

    @Bean(name = ["queueExecuteScheduler"])
    fun queExecuteScheduler(): ScheduledExecutorService = Executors.newScheduledThreadPool(QUE_EXEUTE_POOL_SIZE)

    override fun configureTasks(taskRegistrar: ScheduledTaskRegistrar) {
        taskRegistrar.setScheduler(priceMonitorScheduler())
        taskRegistrar.setScheduler(queExecuteScheduler())
    }

}
