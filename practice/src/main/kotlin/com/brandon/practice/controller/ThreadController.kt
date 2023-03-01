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

   @GetMapping("/stock_price/stop_monitor")
    fun stopMonitor(): CustomizedJsonResult {
        priceCheckService.shutDown()
        orderService.shutDown()
        return CustomizedJsonResult.ok("shutDown all the scheduledJobs")
    }

    @GetMapping("/stock_price/restart_monitor")
    fun restartMonitor(): CustomizedJsonResult {
        priceCheckService.restartScheduler(initial = false)
        orderService.restartScheduler(initial = false)
        return CustomizedJsonResult.ok("successfully restarted monitoring !!")
    }

    @GetMapping("/stock_price/show_price/{acct_id}")
    fun showPriceMap(@PathVariable(value="acct_id") acctId: String ): CustomizedJsonResult {
        priceCheckService.showStockMap(acctId)
        return CustomizedJsonResult.ok(priceCheckService.showStockMap(acctId))
    }


}
