package com.brandon.practice.service

import org.slf4j.Logger
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService

sealed interface CronService {

    var scheduler: ScheduledExecutorService

    fun shutDown(className: String, logger: Logger) {
        logger.info("[${className}] toShutDown Scheduler: ${scheduler.toString()}")
        if(!scheduler.isShutdown){
            logger.info("[${className}] shutdown")
            scheduler.shutdown()
        }
    }

    val POOL_SIZE: Int
    val logger: Logger

    fun reassignSchedule(newScheduler: ScheduledExecutorService)

    fun reassignSchedule()

    fun restartScheduler( className: String, initial: Boolean) {

        // 처음이 아닌 경우면, 이미 scheduler-pool이 존재할 것이다.
        if (!initial){
            logger.info("### this scheduler ${scheduler.toString()}")
            shutDown(className, logger)
            logger.info("[${className}] scheduler shutDown?(${scheduler.isShutdown})")
        }
        // shutDown 되든말든, 일단 새로운 scheduler는 선언되고
        val newScheduler =  Executors.newScheduledThreadPool(POOL_SIZE)

        logger.info("[${className}] has started ")
        reassignSchedule(newScheduler)
    }

}
