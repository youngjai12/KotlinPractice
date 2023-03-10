package com.brandon.practice.service

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

@Service
class OrderService(
    @Qualifier("queueExecuteScheduler")
    private var queExecuteScheduler: ScheduledExecutorService
) : CronService {
    private val logger = LoggerFactory.getLogger(javaClass)
    // 이 pool에서는 굳이 thread별로 관리되어야할 필요가 없는것 같아서..!
    // private val scheduledTaskStatusMap = HashMap<String, ScheduledFuture<*>?>()

    override val POOL_SIZE: Int = 1
    override var scheduler: ScheduledExecutorService = queExecuteScheduler

    init {
        restartScheduler(className = "orderService", initial = true, logger = logger)
    }

    fun executeOrder() {
        val currentThread = Thread.currentThread()
        val threadId = currentThread.id
        val threadName = currentThread.name
        logger.info("orderService: Current thread ID($threadId) name($threadName)")
    }

    override fun reassignSchedule(newScheduler: ScheduledExecutorService) {
        newScheduler.scheduleAtFixedRate({ executeOrder() }, 0L, 3000L, TimeUnit.MILLISECONDS)
    }
}
