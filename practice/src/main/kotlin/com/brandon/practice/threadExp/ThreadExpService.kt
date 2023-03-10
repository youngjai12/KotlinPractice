package com.brandon.practice.threadExp

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.stereotype.Service
import java.lang.RuntimeException
import java.util.concurrent.Executor


@Service
class ThreadExpService(
    val executorPool: ThreadPoolTaskExecutor
) {

    val logger: Logger = LoggerFactory.getLogger(javaClass)

    fun threadExecute() {
        for(i in 0..20) {
            val currentThread = Thread.currentThread()
            val threadId = currentThread.id
            val threadName = currentThread.name
            Thread.sleep(1000L)
            //logger.info("thread(${currentThread}): id(${threadId}) name(${threadName}) - $i th execution")

            print("thread(${currentThread}): id(${threadId}) name(${threadName}) - $i th execution")

        }
        throw RuntimeException("deliberate exception for testing")
    }

    fun initiateThread(acctId: String) {
        val currentThread = Thread.currentThread()
        val threadId = currentThread.id
        val threadName = currentThread.name

        executorPool.submit { threadExecute() }
        logger.info("killed..back to main thread(${currentThread}): id(${threadId}) name(${threadName})")
    }

}
