package com.brandon.practice.service

import org.slf4j.Logger
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService

interface ThreadPoolManager {
    val threadPool: ScheduledExecutorService
    val logger: Logger
    val POOL_SIZE: Int

    fun poolShutDown() {
        logger.info(" toShutDown Scheduler: ${threadPool.toString()}")
        if(!threadPool.isShutdown){
            closeOpendThread()
            threadPool.shutdown()
        }
    }

    fun reAssignThreadOnPool(newThreadPool: ScheduledExecutorService)

    fun restartThreadPool() {
        val newScheduler =  Executors.newScheduledThreadPool(POOL_SIZE)
        reAssignThreadOnPool(newScheduler)
    }
    fun closeOpendThread()
}
