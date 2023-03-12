package com.brandon.practice.threadExp

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.stereotype.Service
import java.lang.Exception
import java.lang.RuntimeException
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Future


@Service
class ThreadExpService(
    val executorPool: ThreadPoolTaskExecutor
) {

    val logger: Logger = LoggerFactory.getLogger(javaClass)
    private val threadStatusMap = ConcurrentHashMap<String, Future<*>?>()

    fun task() {
        for(i in 0..10) {
            val currentThread = Thread.currentThread()
            val threadId = currentThread.id
            val threadName = currentThread.name
            Thread.sleep(700L)
            logger.info("## error occur : thread(${currentThread}): id(${threadId}) name(${threadName}) - $i th execution")
        }
        throw RuntimeException("deliberate exception for testing")
    }

    fun threadExecuteExceptionHandle() {
        try {
            task()
        } catch(e: Exception) {
            logger.error("error happend! ${e.message}")
            //executorPool.submit{ threadExecute() }
        }
    }

    fun exceptionOccurThread() {
        val currentThread = Thread.currentThread()
        val threadId = currentThread.id
        val threadName = currentThread.name

        threadStatusMap["d2"] = executorPool.submit { task() }


        //async하게 실행되므로 이 줄은 그냥 바로 실행된다.
        logger.info("killed..back to main thread(${currentThread}): id(${threadId}) name(${threadName})")
    }

    fun initiateThread(acctId: String) {
        val currentThread = Thread.currentThread()
        val threadId = currentThread.id
        val threadName = currentThread.name

        threadStatusMap["ddd"] = executorPool.submit { threadExecuteExceptionHandle() }


        //async하게 실행되므로 이 줄은 그냥 바로 실행된다.
        logger.info("killed..back to main thread(${currentThread}): id(${threadId}) name(${threadName})")
    }

    fun getThreadStatus():  ConcurrentHashMap<String, Future<*>?> {
        return threadStatusMap
    }



}
