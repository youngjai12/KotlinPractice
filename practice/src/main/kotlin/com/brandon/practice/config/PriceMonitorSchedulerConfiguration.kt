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
<<<<<<<< HEAD:practice/src/main/kotlin/com/brandon/practice/config/PriceMonitorSchedulerConfiguration.kt
class PriceMonitorSchedulerConfiguration : SchedulingConfigurer {
========
class ScheduledConfig: SchedulingConfigurer {
>>>>>>>> feature/20230305_multiple_scheduler:practice/src/main/kotlin/com/brandon/practice/config/ScheduledConfig.kt

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
