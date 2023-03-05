package com.brandon.practice.service

interface CronService {

    fun execute()
    fun shutDown(className: String) {
        logger.info("[OrderService] toShutDown Scheduler: ${queExecuteScheduler.toString()}")
        if(!queExecuteScheduler.isShutdown){
            logger.info("[OrderService] shutdown")
            queExecuteScheduler.shutdown()
        }
    }
    fun restartScheduler(initial: Boolean, threadCount: Int): Unit

}
