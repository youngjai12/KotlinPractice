package com.brandon.practice.service

import org.slf4j.Logger
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
    override val logger: Logger = LoggerFactory.getLogger(javaClass)
    // 이 pool에서는 굳이 thread별로 관리되어야할 필요가 없는것 같아서..!
    // private val scheduledTaskStatusMap = HashMap<String, ScheduledFuture<*>?>()

    override val POOL_SIZE: Int = 1
    override var scheduler: ScheduledExecutorService = queExecuteScheduler

    init {
        restartScheduler(className = "orderService", initial = true)
    }

    fun executeOrder() {
        val currentThread = Thread.currentThread()
        val threadId = currentThread.id
        val threadName = currentThread.name
        logger.info("orderService: Current thread ID($threadId) name($threadName)")
    }

    override fun reassignSchedule(newScheduler: ScheduledExecutorService) {
        scheduler = newScheduler // 처음에 뜰 때 DI받았던 애는 shutdown으로 인해서 없어졋으니까, interface에서 새로 선언했던 애를 받아서 주입

        newScheduler.scheduleAtFixedRate({ executeOrder() }, 0L, 3000L, TimeUnit.MILLISECONDS)
    }

    override fun reassignSchedule() {
        reassignSchedule(scheduler)
    }
}
