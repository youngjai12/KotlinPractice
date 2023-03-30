package com.brandon.practice.controller

import com.brandon.practice.module.CustomizedJsonResult
import com.brandon.practice.service.ConfirmCheckService
import com.brandon.practice.service.OrderService
import com.brandon.practice.service.PriceCheckService
import com.brandon.practice.threadExp.ThreadExpService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class ThreadController(
    private val priceCheckService: PriceCheckService,
    private val orderService: OrderService,
    private val confirmCheckService: ConfirmCheckService,
    private val threadExpService: ThreadExpService
) {

    @GetMapping("/stock_price/restart_monitor/{threadCnt}")
    fun reassignStockMonitor(@PathVariable(value = "threadCnt") threadCnt: Int): CustomizedJsonResult {
        priceCheckService.restartScheduler("ThreadController",false)

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

    @GetMapping("/thread/shut_down/{pool}")
    fun shutDownSpecificPool(@PathVariable(value = "pool") pool: String): CustomizedJsonResult {
        when (pool){
            "order" -> orderService.shutDown("orderService", orderService.logger)
            "confirm" -> confirmCheckService.shutDown("confirmCheckService", confirmCheckService.logger)
            "stock" -> priceCheckService.shutDown("priceCheckService", priceCheckService.logger)
            else -> {
                orderService.shutDown("orderService", orderService.logger)
                confirmCheckService.shutDown("confirmCheckService", confirmCheckService.logger)
            }
        }
        return CustomizedJsonResult.ok("${pool} scheduler successfully shutdown !")
    }

    @GetMapping("thread/restart/{pool}")
    fun restartSpecificPool(@PathVariable(value = "pool") pool: String): CustomizedJsonResult {
        when (pool){
            "order" -> orderService.restartScheduler("orderService", false)
            "confirm" -> confirmCheckService.restartScheduler("confirmCheckService", false)
            "stock" -> priceCheckService.restartScheduler("priceCheckService", false)
            else -> {
                orderService.restartScheduler("orderService", false)
                confirmCheckService.restartScheduler("confirmCheckService", false)
            }
        }
        return CustomizedJsonResult.ok("${pool} scheduler successfully restarted !")
    }

    @GetMapping("/thread/restart/job/{service}")
    fun restartThread(@PathVariable(value = "service") service: String): CustomizedJsonResult {
        when(service) {
            "order" -> orderService.reassignSchedule()
            "confirm" -> confirmCheckService.reassignSchedule()
            "stock" -> priceCheckService.reassignSchedule()
            else -> {
                orderService.reassignSchedule()
                confirmCheckService.reassignSchedule()
            }
        }

        return CustomizedJsonResult.ok("${service} has restarted !")
    }

    @GetMapping("/thread/excpetionOccurThread")
    fun exceptionOccurThread() : CustomizedJsonResult {
        //threadExpService.exceptionOccurThread()
        return CustomizedJsonResult.ok("error_occur thread !")
    }


}
