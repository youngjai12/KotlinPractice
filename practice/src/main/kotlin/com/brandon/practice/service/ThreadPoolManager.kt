package com.brandon.practice.service

import org.slf4j.Logger
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture

interface ThreadPoolManager {
    val threadPool: ScheduledExecutorService
    val logger: Logger
    val POOL_SIZE: Int
    val threadStatusMap: HashMap<String, ScheduledFuture<*>?>

    fun poolShutDown() {
        logger.info(" toShutDown Scheduler: ${threadPool.toString()}")
        if(!threadPool.isShutdown){
            closeOpenedThread()
            threadPool.shutdown()
        }
    }

    fun reAssignThreadOnPool(newThreadPool: ScheduledExecutorService)

    fun restartThreadPool() {
        val newScheduler =  Executors.newScheduledThreadPool(POOL_SIZE)
        reAssignThreadOnPool(newScheduler)
    }

    fun closeOpenedThread() {
        threadStatusMap.values.toList().forEach{
            it?.cancel(true)
        }
        threadStatusMap.clear()
    }
}
