package com.brandon.practice.service

import com.brandon.practice.config.SchedulerConfig
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

@Service
class OrderService(
    var priceCheckScheduler: ScheduledExecutorService
): CronService {
    private val logger = LoggerFactory.getLogger(javaClass)

    init {
        priceCheckScheduler.scheduleAtFixedRate({ executeOrder() }, 0L, 3000L, TimeUnit.MILLISECONDS)
    }

    fun executeOrder() {
        val currentThread = Thread.currentThread()
        val threadId = currentThread.id
        val threadName = currentThread.name
        logger.info("orderService: Current thread ID($threadId) name($threadName)")
    }

    override fun shutDown() {
        logger.info("[OrderService] toShutDown Scheduler: ${priceCheckScheduler.toString()}")
        if(!priceCheckScheduler.isShutdown){
            logger.info("[OrderService] shutdown")
            priceCheckScheduler.shutdown()
        }
    }

    override fun restartScheduler(initial: Boolean) {
        if(!initial){
            logger.info("### this scheduler ${priceCheckScheduler.toString()}")
            shutDown()
            logger.info("[OrderService] scheduler shutDown?(${priceCheckScheduler.isShutdown})")
            if(priceCheckScheduler.isShutdown){
                logger.info("[OrderService] needs to shutdown")
                priceCheckScheduler =  Executors.newScheduledThreadPool(SchedulerConfig.POOL_SIZE)
            }
        }
        logger.info("[OrderService] restart Scheduler: ${priceCheckScheduler.toString()}")
        priceCheckScheduler.scheduleAtFixedRate({ executeOrder() }, 0L, 3000L, TimeUnit.MILLISECONDS)

    }
}
