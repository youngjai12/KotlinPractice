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

    fun restartScheduler( className: String, initial: Boolean) {
        if (!initial){
            logger.info("### this scheduler ${scheduler.toString()}")
            shutDown(className, logger)
            logger.info("[${className}] scheduler shutDown?(${scheduler.isShutdown})")
        }
        // shutDown 되든말든, 일단 새로운 scheduler는 선언돼야함.
        val newScheduler =  Executors.newScheduledThreadPool(POOL_SIZE)


        if(!(scheduler == null) && !scheduler.isShutdown){
            logger.info("[${className}] needs to shutdown")
            reassignSchedule(newScheduler)
        }


    }

}
