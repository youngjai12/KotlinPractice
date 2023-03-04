package com.brandon.practice.controller

import com.brandon.practice.config.SchedulerConfig.Companion.POOL_SIZE
import com.brandon.practice.module.CustomizedJsonResult
import com.brandon.practice.service.OrderService
import com.brandon.practice.service.PriceCheckService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.*

@RestController
class ThreadController(
    private val priceCheckService: PriceCheckService,
    private val orderService: OrderService
) {

   @GetMapping("/threads/stop")
    fun stopThreads(): CustomizedJsonResult {
        priceCheckService.shutDown()
        orderService.shutDown()
        return CustomizedJsonResult.ok("shutDown all the scheduledJobs")
    }

    @GetMapping("/threads/restart")
    fun restartThreads(): CustomizedJsonResult {
        priceCheckService.restartScheduler(initial = false, priceCheckService.threadCount)
        orderService.restartScheduler(initial = false, 1)
        return CustomizedJsonResult.ok("successfully restarted monitoring !!")
    }

    @GetMapping("/stock_price/restart_monitor/{threadCnt}")
    fun reassignStockMonitor(@PathVariable(value = "threadCnt") threadCnt: Int): CustomizedJsonResult {
        priceCheckService.restartScheduler(false, threadCnt)

        return CustomizedJsonResult.ok("su")
    }

    @GetMapping("/stock_price/show_price/{acct_id}")
    fun showPriceMap(@PathVariable(value="acct_id") acctId: String ): CustomizedJsonResult {
        priceCheckService.showStockMap(acctId)
        return CustomizedJsonResult.ok(priceCheckService.showStockMap(acctId))
    }

    @GetMapping("/stock_price/cancel_thread/{acct_id}")
    fun cancelStockThread(@PathVariable(value="acct_id") acctId: String): CustomizedJsonResult {
        val result = priceCheckService.cancelSchedule(acctId)
        return CustomizedJsonResult.ok("cancel monitor thread(${acctId}) result(${result})")
    }

    @GetMapping("/stock_price/start_thread/{acct_id}")
    fun startStockThread(@PathVariable(value="acct_id") acctId: String): CustomizedJsonResult {
        priceCheckService.startSchedule(acctId)
        return CustomizedJsonResult.ok("successfully started thread ${acctId}")
    }


}
