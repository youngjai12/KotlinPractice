package com.brandon.practice.service

import com.brandon.practice.config.SchedulerConfig
import org.slf4j.Logger
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

sealed interface CronService {

    fun shutDown(className: String, logger: Logger, scheduler: ScheduledExecutorService) {
        logger.info("[${className}] toShutDown Scheduler: ${scheduler.toString()}")
        if(!scheduler.isShutdown){
            logger.info("[${className}] shutdown")
            scheduler.shutdown()
        }
    }

    val POOL_SIZE: Int

    fun reassignSchedule(newScheduler: ScheduledExecutorService)

    fun restartScheduler( className: String, initial: Boolean,
                         logger: Logger, scheduler: ScheduledExecutorService) {
        if (!initial){
            logger.info("### this scheduler ${scheduler.toString()}")
            shutDown(className, logger, scheduler)
            logger.info("[${className}] scheduler shutDown?(${scheduler.isShutdown})")
        }
        // shutDown 되든말든, 일단 새로운 scheduler는 선언돼야함.
        val newScheduler =  Executors.newScheduledThreadPool(POOL_SIZE)

        if(!scheduler.isShutdown){
            logger.info("[${className}] needs to shutdown")
            reassignSchedule(newScheduler)
        }


    }

}
