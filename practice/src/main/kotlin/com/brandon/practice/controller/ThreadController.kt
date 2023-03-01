package com.brandon.practice.controller

import com.brandon.practice.config.SchedulerConfig.Companion.POOL_SIZE
import com.brandon.practice.module.CustomizedJsonResult
import com.brandon.practice.service.PriceCheckService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.*

@RestController
class ThreadController(
    private val priceCheckService: PriceCheckService
) {
    @Autowired
    lateinit var priceCheckScheduler: ScheduledExecutorService

    private val logger = LoggerFactory.getLogger(javaClass)

    @GetMapping("/stock_price/stop_monitor")
    fun stopMonitor(): CustomizedJsonResult {
        priceCheckScheduler.shutdown()
        return CustomizedJsonResult.ok("shutDown all the scheduledJobs")
    }

    @GetMapping("/stock_price/restart_monitor")
    fun restartMonitor(): CustomizedJsonResult {
        val acctIdList = listOf("youngjai", "hwang1", "purestar")
        priceCheckScheduler =  Executors.newScheduledThreadPool(POOL_SIZE)

        acctIdList.forEach { acctId ->
            val stockList = priceCheckService.stockMonitorAssign(acctId)
            priceCheckScheduler.scheduleAtFixedRate({ priceCheckService.priceCollect(stockList, acctId) },
                0L, 10L, TimeUnit.MILLISECONDS)
        }

        return CustomizedJsonResult.ok("successfully assinged !!")

    }


   @GetMapping("/stock_price/quit_thread/{acct_id}")
   fun resetThread(@PathVariable(value="acct_id") acctId: String ): CustomizedJsonResult {
        priceCheckService.stopScheduleJob(acctId)
        return CustomizedJsonResult.ok("stop monitoirng ${acctId}\"")
    }


}
