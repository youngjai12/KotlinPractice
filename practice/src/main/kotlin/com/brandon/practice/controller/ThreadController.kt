package com.brandon.practice.controller

import com.brandon.practice.module.CustomizedJsonResult
import com.brandon.practice.service.*
import com.brandon.practice.threadExp.ThreadExpService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class ThreadController(
//    private val priceCheckService: PriceCheckService,
//    private val orderService: OrderService,
//    private val confirmCheckService: ConfirmCheckService,
//    private val threadExpService: ThreadExpService

    private val queueScheduleManager: QueueScheduleManager,
    private val priceScheduleManager: PriceScheduleManager
) {



    @GetMapping("/stock_price/show_price/{acct_id}")
    fun showPriceMap(@PathVariable(value="acct_id") acctId: String ): CustomizedJsonResult {
        val assignedResult = priceScheduleManager.showStockMap(acctId)
        return CustomizedJsonResult.ok(assignedResult)
    }

    @GetMapping("/stock/find_by_stockCd/{stockCd}")
    fun findAcctIdByStockCd(@PathVariable(value="stockCd") stockCd: String): CustomizedJsonResult {
        return try{
            val acctId = priceScheduleManager.findThreadByStockCd(stockCd)
            CustomizedJsonResult.ok("acctId : ${acctId}")
        } catch(e: Exception) {
            when(e){
                is DataNotFoundException -> return CustomizedJsonResult.dataNotFound(e.msg)
                else -> return CustomizedJsonResult.internalError(e.stackTraceToString())
            }
        }
    }


    @GetMapping("/thread/price/cancel/{acct_id}")
    fun cancelStockThread(@PathVariable(value="acct_id") acctId: String): CustomizedJsonResult {
        return try {
            priceScheduleManager.validateAcctId(acctId)
            priceScheduleManager.cancelThread(acctId)
            CustomizedJsonResult.internalError("dfd")
        } catch(e: Exception) {
            when(e) {
                is UnavailableAccountIdException -> return CustomizedJsonResult.dataNotFound(e.msg)
                else -> return CustomizedJsonResult.internalError(e.stackTraceToString())
            }
        }
    }

    @GetMapping("/thread/price/start/{acct_id}")
    fun startStockThread(@PathVariable(value="acct_id") acctId: String): CustomizedJsonResult {
        return try {
            priceScheduleManager.validateAcctId(acctId)
            priceScheduleManager.openOnethread(acctId)
            CustomizedJsonResult.ok("successfully started thread ${acctId}")
        } catch (e: Exception) {
            when(e){
                is UnavailableAccountIdException -> return CustomizedJsonResult.dataNotFound(e.msg)
                else -> return CustomizedJsonResult.internalError(e.stackTraceToString())
            }
        }
    }

    @GetMapping("/thread/queue/start/{queue}")
    fun startQueueThread(@PathVariable(value="queue") queue: String): CustomizedJsonResult {
        return try{
            queueScheduleManager.openThread(queue)
            CustomizedJsonResult.ok("successfully started ${queue}")
        } catch(e: Exception) {
            when(e) {
                is IllegalArgumentException -> CustomizedJsonResult.dataNotFound(e.message)
                else -> return CustomizedJsonResult.internalError(e.stackTraceToString())
            }
        }
    }

    @GetMapping("/thread/queue/cancel/{queue}")
    fun cancelQueueThread(@PathVariable(value="queue") queue: String): CustomizedJsonResult {
        return try{
            val result = queueScheduleManager.closeThread(queue)
            if(result){
                CustomizedJsonResult.ok("successfully canceled queue(${queue})")
            } else {
                CustomizedJsonResult.okButWarn("${queue} has not been assigned. No need to cancel")
            }
        } catch(e: Exception) {
            when(e){
                is IllegalArgumentException -> {
                    CustomizedJsonResult.dataNotFound("${queue} not found: ${e.message}")
                }
                else -> {
                    CustomizedJsonResult.internalError("${queue} ${e.stackTraceToString()}")
                }
            }
        }
    }

    @GetMapping("/pool/close/{pool}")
    fun shutDownSpecificPool(@PathVariable(value = "pool") pool: String): CustomizedJsonResult {
        when (pool){
            "queue" -> queueScheduleManager.poolShutDown()
            "price" -> priceScheduleManager.poolShutDown()
            else -> return CustomizedJsonResult.dataNotFound("${pool} you entered is not found")
        }
        return CustomizedJsonResult.ok("${pool} scheduler successfully shutdown !")
    }

    @GetMapping("pool/restart/{pool}")
    fun restartSpecificPool(@PathVariable(value = "pool") pool: String): CustomizedJsonResult {
        when (pool){
            "queue" -> queueScheduleManager.restartThreadPool()
            "price" -> priceScheduleManager.restartThreadPool()
            else -> return CustomizedJsonResult.dataNotFound("${pool} you entered is not found")
        }
        return CustomizedJsonResult.ok("${pool} scheduler successfully shutdown !")
    }
}
