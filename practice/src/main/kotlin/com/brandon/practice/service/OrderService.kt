package com.brandon.practice.service

import com.brandon.practice.config.PriceMonitorSchedulerConfiguration
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

@Service
class OrderService(
    var customizedScheduler: ScheduledExecutorService
): CronService {
    private val logger = LoggerFactory.getLogger(javaClass)

    init {
        customizedScheduler.scheduleAtFixedRate({ executeOrder() }, 0L, 3000L, TimeUnit.MILLISECONDS)
    }

    fun executeOrder() {
        val currentThread = Thread.currentThread()
        val threadId = currentThread.id
        val threadName = currentThread.name
        logger.info("orderService: Current thread ID($threadId) name($threadName)")
    }

    override fun shutDown() {
        logger.info("[OrderService] toShutDown Scheduler: ${customizedScheduler.toString()}")
        if(!customizedScheduler.isShutdown){
            logger.info("[OrderService] shutdown")
            customizedScheduler.shutdown()
        }
    }

    override fun restartScheduler(initial: Boolean, threadCount: Int) {
        if(!initial){
            logger.info("### this scheduler ${customizedScheduler.toString()}")
            shutDown()
            logger.info("[OrderService] scheduler shutDown?(${customizedScheduler.isShutdown})")
            if(customizedScheduler.isShutdown){
                logger.info("[OrderService] needs to shutdown")
                customizedScheduler =  Executors.newScheduledThreadPool(PriceMonitorSchedulerConfiguration.POOL_SIZE)
            }
        }
        logger.info("[OrderService] restart Scheduler: ${customizedScheduler.toString()}")
        customizedScheduler.scheduleAtFixedRate({ executeOrder() }, 0L, 3000L, TimeUnit.MILLISECONDS)

    }
}
