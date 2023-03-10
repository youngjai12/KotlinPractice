package com.brandon.practice.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import java.lang.Exception
import java.lang.RuntimeException
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Future
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

@Service
class ConfirmCheckService(
    @Qualifier("queueExecuteScheduler")
    private var confirmQueScheduler: ScheduledExecutorService
): CronService {
    override val logger: Logger = LoggerFactory.getLogger(javaClass)
    // private lateinit var confirmQueScheduler: ScheduledExecutorService
    private val threadStatusMap = ConcurrentHashMap<String, Future<*>?>()

    init {
        restartScheduler(className = "ConfirmCheckService", initial = true)
    }

    override var scheduler: ScheduledExecutorService = confirmQueScheduler


   fun execute() {
        val currentThread = Thread.currentThread()
        val threadId = currentThread.id
        val threadName = currentThread.name
        logger.info("## ConfirmCheckService: Current thread ID($threadId) name($threadName)")
    }


    fun execute2() {
        try {
            task()
        } catch(e: Exception) {
            logger.error("error happend! ${e.message}")
            // scheduler.submit{ execute2() }
            // reassignSchedule(scheduler)
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

    override fun reassignSchedule(newScheduler: ScheduledExecutorService) {
        scheduler = newScheduler
        //newScheduler.scheduleAtFixedRate({ execute2() }, 0L, 3000L, TimeUnit.MILLISECONDS)
        newScheduler.scheduleAtFixedRate( { task() },  0L, 5000L, TimeUnit.MILLISECONDS)

    }

    override fun reassignSchedule() {
        reassignSchedule(scheduler)
    }

    override val POOL_SIZE: Int = 1


}
