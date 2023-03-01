package com.brandon.practice.service

import com.brandon.practice.hantoo.HantooClient
import com.brandon.practice.hantoo.HantooPriceTemplate
import org.slf4j.LoggerFactory
import com.brandon.practice.domain.PriceAt
import java.util.concurrent.ConcurrentHashMap

class PriceCheckService(
    val hantooClient: HantooClient,
) {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val currentPriceInfo = ConcurrentHashMap<String, PriceAt>()

    val CHUNK_SIZE = 10


    companion object{
        val MIXED_STOCK_SAMPLE = listOf("002420", "002820", "006880", "008500",
            "MSFT", "033250", "079190", "INTC", "101400")

        val MIXED_STOCK_SAMPLE_V2 = listOf("104460", "110020", "NVDA", "140910", "AAPL", "191410", "263920")
        val MIXED_STOCK_SAMPLE_V3 = listOf("001820", "006340", "REGN", "039560", "META", "066430", "AMZN")

    }

    fun stockMonitorAssign(acctId: String): List<String> {
        return when(acctId){
            "youngjai" -> MIXED_STOCK_SAMPLE
            "purestar" -> MIXED_STOCK_SAMPLE_V2
            "hwang1" -> MIXED_STOCK_SAMPLE_V3
            else -> listOf("null")
        }
    }

    fun showStockMap(acctId: String): String {
        val stockList = stockMonitorAssign(acctId)
        var resultStr = ""
        for(stockCd in stockList){
            resultStr = resultStr + "${stockCd}(${currentPriceInfo[stockCd]?.price} @ ${currentPriceInfo[stockCd]?.at})"
        }
        return resultStr
    }

    // Mono 형태로 return 해야, 합성이 쉬움
    fun getPriceMono(stockCd: String, acctId: String): Mono<HantooPriceTemplate.PriceResponse> {
        val appKey = userInfo.getAppKey(acctId)!!
        val appsecret = userInfo.getAppSecret(acctId)!!
        val accessToken = userInfo.getAccessToken(acctId)!!

        val priceRequestForm = if(!stockCd.matches("[a-zA-Z]+".toRegex())){
            HantooPriceTemplate.DomesticPriceRequest(
                request = HantooPriceTemplate.DomesticPriceRequest.Request(fid_input_iscd = stockCd),
                header = HantooPriceTemplate.DomesticPriceRequest.Header(appkey = appKey, appsecret = appsecret,
                    authorization =  "Bearer ${accessToken}")
            )
        } else {
            HantooPriceTemplate.OverseaPriceRequest(
                request = HantooPriceTemplate.OverseaPriceRequest.Request(symb = stockCd, excd = "NAS"),
                header = HantooPriceTemplate.OverseaPriceRequest.Header(appkey = appKey, appsecret = appsecret,
                authorization =  "Bearer ${accessToken}")
            )
        }
        return hantooClient.getPrice(priceRequestForm)
    }

    fun priceCollect(stockList: List<String>, acctId: String) {
        stockList.chunked(CHUNK_SIZE).forEach{ chunkedStockList ->
            Thread.sleep(970)
            var priceTraceString = ""
            Flux.fromIterable(chunkedStockList).flatMap { stockCd ->
                val priceInfo: Mono<HantooPriceTemplate.PriceResponse> = getPriceMono(stockCd, acctId).onErrorResume {
                    when(it) {
                        is HantooPriceTemplate.PostException ->
                            logger.error("$stockCd HantooApi Error ${it.message} ${it.msg22}")
                        else ->
                            logger.error("$stockCd Unknown Error ${it.message}")
                    }
                    when(stockCd.matches("[a-zA-Z]+".toRegex())){
                        false ->
                            Mono.just(HantooPriceTemplate.DomesticPriceRequest.Response())
                        else ->
                            Mono.just(HantooPriceTemplate.OverseaPriceRequest.Response())
                    }
                }
                val stockMono = Mono.just(stockCd)
                Mono.zip(stockMono, priceInfo).map{ tuple ->
                    val stockCd = tuple.t1
                    val priceInfo = tuple.t2
                    PriceAt(stockCd = stockCd, price = priceInfo.currentPrice(), priceUnit = priceInfo.priceUnit())
                }
            }.collectList().block()?.map {
                priceTraceString = priceTraceString +"${it.stockCd}(${it.price}) "
                currentPriceInfo[it.stockCd] = it
            }
            logger.info("${acctId} - ${priceTraceString}")

        }
    }

    @Async("youngjai_thread")
    @Scheduled(fixedDelay = 10)
    fun getPriceAsync1() {
        priceCollect(MIXED_STOCK_SAMPLE, "youngjai")
        val currentThread = Thread.currentThread()
        val threadId = currentThread.id
        val threadName = currentThread.name
        val threadState = currentThread.state
        println("youngjai: Current thread ID: $threadId")
        println("youngjai: Current thread name: $threadName")
        println("youngjai: Current thread state: $threadState")
    }

    @Async("purestar_thread")
    @Scheduled(fixedDelay = 10)
    fun getPriceAsync2() {
        val currentThread = Thread.currentThread()
        val threadId = currentThread.id
        val threadName = currentThread.name
        val threadState = currentThread.state
        println("purestar : Current thread ID: $threadId")
        println("purestar : Current thread name: $threadName")
        println("purestar : Current thread state: $threadState")
        priceCollect(MIXED_STOCK_SAMPLE_V2, "purestar")
    }

    @Async("hwang1_thread")
    @Scheduled(fixedDelay = 10)
    fun getPriceAsync3() {
        val currentThread = Thread.currentThread()
        val threadId = currentThread.id
        val threadName = currentThread.name
        val threadState = currentThread.state
        println("hwang1 : Current thread ID: $threadId")
        println("hwang1 : Current thread name: $threadName")
        println("hwang1 : Current thread state: $threadState")
        priceCollect(MIXED_STOCK_SAMPLE_V3, "hwang1")
    }


//    fun getPriceMono(stockCd: String) {
//        val priceRequestForm = if(stockCd.matches("[a-zA-Z]+".toRegex())){
//            HantooPriceTemplate.DomesticPriceRequest(
//                request = HantooPriceTemplate.DomesticPriceRequest.Request(fid_input_iscd = stockCd),
//                header = HantooPriceTemplate.DomesticPriceRequest.Header()
//            )
//        } else {
//            HantooPriceTemplate.OverseaPriceRequest(
//                request = HantooPriceTemplate.OverseaPriceRequest.Request(symb = stockCd),
//                header = HantooPriceTemplate.OverseaPriceRequest.Header()
//            )
//        }
//        hantooClient.getPrice(priceRequestForm)
//    }
}
