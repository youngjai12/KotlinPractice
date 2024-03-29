package com.brandon.practice.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.lang.Exception
import java.lang.RuntimeException
import java.util.concurrent.*

@Service
class ConfirmCheckService(
    @Qualifier("queueExecuteScheduler")
    private var confirmQueScheduler: ScheduledExecutorService
): ScheduleType.QueueScheduler {
    val logger: Logger = LoggerFactory.getLogger(javaClass)

    override fun execute() {
        try {
            task()
        } catch(e: Exception) {
            logger.error("error happend! ${e.message}")
        }
    }

    fun task() {
        for(i in 0..6) {
            val currentThread = Thread.currentThread()
            val threadId = currentThread.id
            val threadName = currentThread.name
            Thread.sleep(600L)

            logger.info(" ### thread(${currentThread}): id(${threadId}) name(${threadName}) - $i th execution")

        }
        throw RuntimeException("deliberate exception for testing")
    }

}
