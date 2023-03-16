package com.brandon.practice.service

import com.brandon.practice.hantoo.HantooClient
import com.brandon.practice.hantoo.HantooPriceTemplate
import com.brandon.practice.module.UserInfoProperties
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.lang.Exception
import java.util.concurrent.*
import kotlin.math.floor

@Service
class PriceCheckService(
    val hantooClient: HantooClient,
    val userInfo: UserInfoProperties,
    @Qualifier("priceMonitorScheduler")
    private var priceMonitorscheduler: ScheduledExecutorService,
): CronService {
    override val logger = LoggerFactory.getLogger(javaClass)
    private val currentPriceInfo = ConcurrentHashMap<String, PriceAt>()
    private val stockAssingedMap = HashMap<String, List<String>>()
    private val stockAssingedMapV2 = HashMap<String, List<Stock>>()

    private val scheduledTaskStatusMap = HashMap<String, ScheduledFuture<*>?>()

    override var scheduler: ScheduledExecutorService = priceMonitorscheduler

    companion object{
        val THREAD_COUNT = 4
        val CHUNK_SIZE = 15
        val MIXED_STOCK_SAMPLE = listOf(Stock("002420"),  Stock("000150", "SZS"),
            Stock("006880"), Stock("002005", "SZS"), Stock("104460"),
            Stock("002504", "SZS"), Stock("008500"), Stock("MSFT", "NAS"),
            Stock("033250"), Stock("079190"), Stock("INTC", "NAS"),
            Stock("600519", "SHS"), Stock("NVDA", "NAS"),  Stock("191410"),
            Stock("600781", "SHS"), Stock("036670"), Stock("600242", "SHS"),
            Stock("600157", "SZS"))

        val MIXED_STOCK_SAMPLE_V2 = listOf("104460", "110020", "NVDA", "140910", "AAPL", "191410", "263920")
        val MIXED_STOCK_SAMPLE_V3 = listOf("001820", "006340", "REGN", "039560", "META", "066430", "AMZN")
        val MIXED_STOCK_SAMPLE_V4 = listOf("010100", "016580", "AVGO", "036670", "036800", "ASML", "TSLA", "023910")
        val TOT_STOCK_LIST = MIXED_STOCK_SAMPLE_V2.plus(MIXED_STOCK_SAMPLE_V3).plus(MIXED_STOCK_SAMPLE_V4)
    }
    override val POOL_SIZE: Int = 5

    init{
        restartScheduler(className = "PriceCherService", initial = true)
    }

    fun cancelSchedule(acctId: String): Boolean {
        // scheduledFuture.cancel() 하면
        //it is removed from the ScheduledExecutorService's task queue and will not be executed in the future.
        return scheduledTaskStatusMap[acctId]?.let {
            it.cancel(true)
            scheduledTaskStatusMap[acctId] = null
             true
        } ?: false
    }

    // 기본적인 CronService 에서 제공하는 것과는 다르게, api로 개개의 acctId별 thread만 켜고싶을떄.
    fun startSchedule(acctId: String) {
        scheduledTaskStatusMap[acctId] = scheduler.scheduleAtFixedRate({ execute(stockAssingedMap[acctId]!!, acctId) },
            0L, 10L, TimeUnit.MILLISECONDS)
    }

    fun showStockMap(acctId: String): String {
        val stockList = stockAssingedMapV2[acctId]
        var resultStr = ""

        stockList?.let{
            for(stockCd in it){
                resultStr = resultStr + "${stockCd}(${currentPriceInfo[stockCd.stockCd]?.price} @ ${currentPriceInfo[stockCd.stockCd]?.at})"
            }
        } ?: run {
            resultStr = "none"
        }
        return resultStr
    }

    override fun reassignSchedule(newScheduler: ScheduledExecutorService) {
        val tmpAcctIdList = listOf("youngjai", "hwang1", "purestar", "shantf2")
        stockAssignAlgorithm(tmpAcctIdList.take(THREAD_COUNT), MIXED_STOCK_SAMPLE)

        scheduler = newScheduler

        for((acctId, subStockList) in stockAssingedMapV2) {
            scheduledTaskStatusMap[acctId] = scheduler.scheduleAtFixedRate({ scheduledFunction(subStockList, acctId) },
                0L, 10L, TimeUnit.MILLISECONDS)
        }
    }

    private fun stockAssignAlgorithm(availableAcct: List<String>, stockList: List<Stock>) {
        val perAssignedCnt = floor(MIXED_STOCK_SAMPLE.size.toDouble() / THREAD_COUNT.toDouble()).toInt()
        val tmpAssigned = stockList.chunked(perAssignedCnt).toMutableList()

        var idx =0
        //마지막 남은 종목들 다시 분배하는것
        for(remnant in tmpAssigned.last()) {
            tmpAssigned[idx] =  tmpAssigned[idx].plus(remnant)
            idx = ((idx+1) % THREAD_COUNT)
        }

        // 마지막 꼬다리 종목들 분배완료 후 각 acct_id별로 map 만들어주기
        tmpAssigned.subList(0, tmpAssigned.size-1).forEachIndexed{ index , subStockList ->
            val acctId: String = availableAcct[index]
            stockAssingedMapV2[acctId] = subStockList
        }
        logger.info("AssignedMap: ${stockAssingedMapV2}")
    }

    override fun reassignSchedule() {
        reassignSchedule(scheduler)
    }

    fun scheduledFunction(stockList: List<Stock>, acctId: String) {
        try {
            executeV2(stockList, acctId)
        } catch (e: Exception){
            logger.error("error happend! ${e.message} ${e.stackTraceToString()}")
        }

    }
    // Mono 형태로 return 해야, 합성이 쉬움
    fun getPriceMono(stockCd: String, acctId: String, marketCode: String?=null): Mono<HantooPriceTemplate.PriceResponse> {
        val appKey = userInfo.getAppKey(acctId)!!
        val appsecret = userInfo.getAppSecret(acctId)!!
        val accessToken = userInfo.getAccessToken(acctId)!!

        val priceRequestForm = if(marketCode.isNullOrEmpty()){
            HantooPriceTemplate.DomesticPriceRequest(
                request = HantooPriceTemplate.DomesticPriceRequest.Request(fid_input_iscd = stockCd),
                header = HantooPriceTemplate.DomesticPriceRequest.Header(appkey = appKey, appsecret = appsecret,
                    authorization =  "Bearer ${accessToken}")
            )
        } else {
            HantooPriceTemplate.OverseaPriceRequest(
                request = HantooPriceTemplate.OverseaPriceRequest.Request(symb = stockCd, excd = marketCode),
                header = HantooPriceTemplate.OverseaPriceRequest.Header(appkey = appKey, appsecret = appsecret,
                authorization =  "Bearer ${accessToken}")
            )
        }
        return hantooClient.getPrice(priceRequestForm)
    }

    fun executeV2(stockList: List<Stock>, acctId: String) {
        stockList.chunked(CHUNK_SIZE).forEach{ chunkedStockList ->
            Thread.sleep(970)
            var priceTraceString = ""
            Flux.fromIterable(chunkedStockList).flatMap { stockCd ->
                val priceInfo: Mono<HantooPriceTemplate.PriceResponse> = getPriceMono(stockCd.stockCd, acctId, stockCd.marketCode).onErrorResume {
                    when(it) {
                        is HantooPriceTemplate.PostException ->
                            logger.error("$stockCd HantooApi Error ${it.message} ${it.msg22}")
                        else ->
                            logger.error("$stockCd Unknown Error ${it.message}")
                    }
                    when(stockCd.marketCode.isNullOrEmpty()){
                        true ->
                            Mono.just(HantooPriceTemplate.DomesticPriceRequest.Response())
                        else ->
                            Mono.just(HantooPriceTemplate.OverseaPriceRequest.Response())
                    }
                }
                val stockMono = Mono.just(stockCd)
                Mono.zip(stockMono, priceInfo).map{ tuple ->
                    val stockCd = tuple.t1
                    val priceInfo = tuple.t2
                    PriceAt(stockCd = stockCd.stockCd, price = priceInfo.currentPrice(), priceUnit = priceInfo.priceUnit())
                }
            }.collectList().block()?.map {
                priceTraceString = priceTraceString +"${it.stockCd}(${it.price}) "
                currentPriceInfo[it.stockCd] = it
            }
            logger.info("${acctId} - ${priceTraceString}")

        }
    }

    fun execute(stockList: List<String>, acctId: String) {
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



}

data class Stock(
    val stockCd: String,
    val marketCode: String? = null
)

data class PriceAt(
    val stockCd: String,
    val price: String = "default",
    val at : Long = System.currentTimeMillis()/1000,
    val priceUnit: String
)
