package com.brandon.practice.service

import com.brandon.practice.config.SchedulerConfig
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

@Service
class ConfirmCheckService (
    @Qualifier("queueExecuteScheduler")
    var queExecuteScheduler: ScheduledExecutorService
): CronService {
    private val logger = LoggerFactory.getLogger(javaClass)

    init {
        queExecuteScheduler.scheduleAtFixedRate({ confirmCheck() }, 0L, 5000L, TimeUnit.MILLISECONDS)
    }

    fun confirmCheck() {
        val currentThread = Thread.currentThread()
        val threadId = currentThread.id
        val threadName = currentThread.name
        logger.info("## ConfirmCheckService: Current thread ID($threadId) name($threadName)")
    }

    override fun shutDown() {
        logger.info("[ConfirmCheckService] toShutDown Scheduler: ${queExecuteScheduler.toString()}")
        if(!queExecuteScheduler.isShutdown){
            logger.info("[ConfirmCheckService] shutdown")
            queExecuteScheduler.shutdown()
        }
    }

    override fun restartScheduler(initial: Boolean, threadCount: Int) {
        if (!initial){
            logger.info("### this scheduler ${queExecuteScheduler.toString()}")
            shutDown()
            logger.info("[OrderService] scheduler shutDown?(${queExecuteScheduler.isShutdown})")
            if(queExecuteScheduler.isShutdown){
                logger.info("[OrderService] needs to shutdown")
                queExecuteScheduler =  Executors.newScheduledThreadPool(SchedulerConfig.POOL_SIZE)
            }
        }
        logger.info("[OrderService] restart Scheduler: ${queExecuteScheduler.toString()}")
        queExecuteScheduler.scheduleAtFixedRate({ confirmCheck() }, 0L, 5000L, TimeUnit.MILLISECONDS)
    }

}
