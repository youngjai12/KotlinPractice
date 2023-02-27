package com.brandon.practice.controller

import com.brandon.practice.module.CustomizedJsonResult
import org.slf4j.LoggerFactory
import org.springframework.scheduling.TaskScheduler
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.ScheduledFuture

@RestController
class ThreadController {
    private lateinit var taskScheduler: TaskScheduler
    private var scheduledFuture: ScheduledFuture<*>? = null

    private val logger = LoggerFactory.getLogger(javaClass)

   @GetMapping("/stock_price/thread_reset")
   fun resetThread(): CustomizedJsonResult {
       scheduledFuture?.cancel(true)

       val currentThread = Thread.currentThread()
       val threadId = currentThread.id
       val threadName = currentThread.name
       val threadState = currentThread.state
       logger.info("ThreadController: Current thread ID: $threadId")
       logger.info("ThreadController: Current thread name: $threadName")
       logger.info("ThreadController: Current thread state: $threadState")

       return CustomizedJsonResult.ok("Okay")
   }


}
