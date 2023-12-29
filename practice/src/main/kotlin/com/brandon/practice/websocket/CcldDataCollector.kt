package com.brandon.practice.websocket

import com.brandon.practice.repository.OrderReceiptRepository
import com.brandon.practice.repository.UserAccessInfoRepository
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.DependsOn
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors

@Service
@DependsOn("rdbMemberRepository")
class CcldDataCollector(
    private val userAccessInfoRepository: UserAccessInfoRepository,
    private val hantooSocketClient: HantooSocketClient,
    private val orderReceiptRepository: OrderReceiptRepository
) {

    private val logger = LoggerFactory.getLogger(javaClass)
    var openStockCcldMap = ConcurrentHashMap<String, List<String>>()

    init {
        val availableAccts = userAccessInfoRepository.getAvailableAcctIdList()
        val totStocks = orderReceiptRepository.getStockListByCount(130)
        val stockCcldMap = stockAssignAlgorithm(availableAccts, totStocks)
        logger.info("inited CcldDataCollector")
        assignWorkToThread(stockCcldMap)
        openStockCcldMap = stockCcldMap
    }

    fun getStockAssignedMap(): ConcurrentHashMap<String, List<String>> {
        return openStockCcldMap
    }

    private fun stockAssignAlgorithm(availableAccts: List<String>, totStocks: List<String>): ConcurrentHashMap<String, List<String>> {
        val stockCcldMap = ConcurrentHashMap<String, List<String>>()
        val acctSize = availableAccts.size
        val chunkedStocks =totStocks.chunked(acctSize)

        for ((idx, acctId) in availableAccts.withIndex()) {
            stockCcldMap[acctId] = chunkedStocks[idx]
        }
        return stockCcldMap
    }

    final fun assignWorkToThread(stockCcldMap: ConcurrentHashMap<String, List<String>>) {
        val executorService = Executors.newFixedThreadPool(8)
        // val stockMap = getStockAssignMap()
        for(element in stockCcldMap) {
            val acctId = element.key
            userAccessInfoRepository.getUserInfoByAcctId(acctId)?.approvalKey?.let {
                executorService.submit {
                    hantooSocketClient.collectCcld(
                        acctId = acctId,
                        approvalKey = it,
                        stockList = stockCcldMap[acctId]!!
                    )
                }
            }

        }
    }

}