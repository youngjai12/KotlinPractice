package com.brandon.practice.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

@Service
class ConfirmCheckService(
    @Qualifier("queueExecuteScheduler")
    private var confirmQueScheduler: ScheduledExecutorService
): CronService {
    private val logger: Logger = LoggerFactory.getLogger(javaClass)
    // private lateinit var confirmQueScheduler: ScheduledExecutorService

    init {
        restartScheduler(className = "orderService", initial = true, logger = logger, scheduler = confirmQueScheduler)
    }

   fun execute() {
        val currentThread = Thread.currentThread()
        val threadId = currentThread.id
        val threadName = currentThread.name
        logger.info("## ConfirmCheckService: Current thread ID($threadId) name($threadName)")
    }

    override fun reassignSchedule(newScheduler: ScheduledExecutorService) {
        newScheduler.scheduleAtFixedRate({ execute() }, 0L, 3000L, TimeUnit.MILLISECONDS)
    }

    override val POOL_SIZE: Int = 1


}
