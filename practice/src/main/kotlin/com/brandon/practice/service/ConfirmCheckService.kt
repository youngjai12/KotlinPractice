package com.brandon.practice.service

import com.brandon.practice.config.SchedulerConfig
import org.slf4j.Logger
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
    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    init {
        queExecuteScheduler.scheduleAtFixedRate({ execute() }, 0L, 5000L, TimeUnit.MILLISECONDS)
    }

    override fun execute() {
        val currentThread = Thread.currentThread()
        val threadId = currentThread.id
        val threadName = currentThread.name
        logger.info("## ConfirmCheckService: Current thread ID($threadId) name($threadName)")
    }

    override fun reassignSchedule(className: String) {
        queExecuteScheduler =  Executors.newScheduledThreadPool(SchedulerConfig.POOL_SIZE)
        queExecuteScheduler.scheduleAtFixedRate({ execute() }, 0L, 5000L, TimeUnit.MILLISECONDS)
    }

}
