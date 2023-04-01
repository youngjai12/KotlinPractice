package com.brandon.practice.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import java.lang.RuntimeException
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread
import kotlin.math.floor

@Service
class PriceScheduleManager(
    @Qualifier("priceMonitorScheduler")
    private var priceMonitorThreadPool: ScheduledExecutorService,
    private val priceCheckService: PriceCheckService
): ThreadPoolManager {
    override val threadStatusMap = HashMap<String, ScheduledFuture<*>?>()
    private val stockAssingedMap = HashMap<String, List<Stock>>()

    companion object{
        val CHUNK_SIZE = 15
        val MIXED_STOCK_SAMPLE = listOf(Stock("002420"),  Stock("000150", "SZS"),
            Stock("006880"), Stock("002005", "SZS"), Stock("104460"),
            Stock("002504", "SZS"), Stock("008500"), Stock("MSFT", "NAS"),
            Stock("033250"), Stock("079190"), Stock("INTC", "NAS"),
            Stock("600519", "SHS"), Stock("NVDA", "NAS"),  Stock("191410"),
            Stock("600781", "SHS"), Stock("036670"), Stock("600242", "SHS"),
            Stock("600157", "SZS"))
        val AVAILABLE_ACCT = listOf("youngjai", "hwang1", "purestar", "shantf2")
        val MIXED_STOCK_SAMPLE_V2 = listOf("104460", "110020", "NVDA", "140910", "AAPL", "191410", "263920")
        val MIXED_STOCK_SAMPLE_V3 = listOf("001820", "006340", "REGN", "039560", "META", "066430", "AMZN")
        val MIXED_STOCK_SAMPLE_V4 = listOf("010100", "016580", "AVGO", "036670", "036800", "ASML", "TSLA", "023910")
        val TOT_STOCK_LIST = MIXED_STOCK_SAMPLE_V2.plus(MIXED_STOCK_SAMPLE_V3).plus(MIXED_STOCK_SAMPLE_V4)
    }
    override lateinit var threadPool: ScheduledExecutorService
    override val logger: Logger = LoggerFactory.getLogger(javaClass)

    override val POOL_SIZE: Int = 4

    override fun reAssignThreadOnPool(newThreadPool: ScheduledExecutorService) {
        threadPool = newThreadPool
        stockAssignAlgorithm(AVAILABLE_ACCT, MIXED_STOCK_SAMPLE)
        assignThread()
    }

    init {
        threadPool = priceMonitorThreadPool
        stockAssignAlgorithm(AVAILABLE_ACCT, MIXED_STOCK_SAMPLE)
        assignThread()
    }

    final fun assignThread() {
        AVAILABLE_ACCT.forEach{ openOnethread(it) }
    }

    fun cancelThread(acctId: String): Boolean {
        return threadStatusMap[acctId]?.let{
            it.cancel(true)
            threadStatusMap.remove(acctId)
            true
        } ?: false
    }

    fun openOnethread(acctId: String) {
        val acctAssignedStockList = stockAssingedMap[acctId]!!
        threadStatusMap.getOrPut(acctId){
            threadPool.scheduleAtFixedRate({ priceCheckService.execute(acctAssignedStockList, acctId) },
                0L, 10L, TimeUnit.MILLISECONDS)
        }
    }

    fun findThreadByStockCd(stockCd: String): String =
        stockAssingedMap.entries.firstOrNull{ item ->
            item.value.any {stock -> stock.stockCd == stockCd}
        }?.key ?: throw DataNotFoundException("${stockCd} is not assigned to any thread")


    private fun stockAssignAlgorithm(availableAcct: List<String>, stockList: List<Stock>) {
        val perAssignedCnt = floor(MIXED_STOCK_SAMPLE.size.toDouble() / POOL_SIZE.toDouble()).toInt()
        val tmpAssigned = stockList.chunked(perAssignedCnt).toMutableList()

        var idx =0
        //마지막 남은 종목들 다시 분배하는것
        for(remnant in tmpAssigned.last()) {
            tmpAssigned[idx] =  tmpAssigned[idx].plus(remnant)
            idx = ((idx+1) % POOL_SIZE)
        }

        // 마지막 꼬다리 종목들 분배완료 후 각 acct_id별로 map 만들어주기
        tmpAssigned.subList(0, tmpAssigned.size-1).forEachIndexed{ index , subStockList ->
            val acctId: String = availableAcct[index]
            stockAssingedMap[acctId] = subStockList
        }
        logger.info("AssignedMap: ${stockAssingedMap}")
    }

    fun showStockMap(acctId: String): String {
        val stockList = stockAssingedMap[acctId]
        var resultStr = ""

        stockList?.let{
            for(stock in it){
                val priceInfo = priceCheckService.getPriceInfoByStock(stock)
                resultStr = resultStr + "${stock.stockCd}(${priceInfo?.price} @ ${priceInfo?.at})"
            }
        } ?: run {
            resultStr = "none"
        }
        return resultStr
    }

    fun validateAcctId(acctId: String){
        if(!AVAILABLE_ACCT.contains(acctId)){
            throw UnavailableAccountIdException("${acctId} is not available accountId")
        }
    }

}

data class UnavailableAccountIdException(val msg: String): RuntimeException()
data class DataNotFoundException(val msg: String): RuntimeException()

