package com.brandon.practice.service

import com.brandon.practice.hantoo.HantooClient
import com.brandon.practice.hantoo.HantooPriceTemplate
import com.brandon.practice.module.UserInfoProperties
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.concurrent.*
import kotlin.math.ceil

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
    private val scheduledTaskStatusMap = HashMap<String, ScheduledFuture<*>?>()

    val threadCount = 3

    override var scheduler: ScheduledExecutorService = priceMonitorscheduler

    companion object{
        val CHUNK_SIZE = 3
        val MIXED_STOCK_SAMPLE = listOf("002420", "002820", "006880", "008500", "MSFT", "033250", "079190", "INTC", "101400")
        val MIXED_STOCK_SAMPLE_V2 = listOf("104460", "110020", "NVDA", "140910", "AAPL", "191410", "263920")
        val MIXED_STOCK_SAMPLE_V3 = listOf("001820", "006340", "REGN", "039560", "META", "066430", "AMZN")
        val MIXED_STOCK_SAMPLE_V4 = listOf("010100", "016580", "AVGO", "036670", "036800", "ASML", "TSLA", "023910")
        val TOT_STOCK_LIST = MIXED_STOCK_SAMPLE.plus(MIXED_STOCK_SAMPLE_V2).plus(MIXED_STOCK_SAMPLE_V3)
            .plus(MIXED_STOCK_SAMPLE_V4)
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
        val stockList = stockAssingedMap[acctId]
        var resultStr = ""

        stockList?.let{
            for(stockCd in it){
                resultStr = resultStr + "${stockCd}(${currentPriceInfo[stockCd]?.price} @ ${currentPriceInfo[stockCd]?.at})"
            }
        } ?: run {
            resultStr = "none"
        }
        return resultStr
    }

    override fun reassignSchedule(newScheduler: ScheduledExecutorService) {
        val tmpAcctIdList = listOf("youngjai", "hwang1", "purestar", "shantf2")
        val availableAcct = tmpAcctIdList.take(threadCount)

        val perAssignedCnt = ceil(TOT_STOCK_LIST.size.toDouble() / threadCount.toDouble()).toInt()
        scheduler = newScheduler

        TOT_STOCK_LIST.chunked(perAssignedCnt).forEachIndexed { idx, subStockList ->
            val acctId: String = availableAcct[idx]
            stockAssingedMap[acctId] = subStockList
            scheduledTaskStatusMap[acctId] = scheduler.scheduleAtFixedRate({ execute(subStockList, acctId) },
                0L, 10L, TimeUnit.MILLISECONDS)
        }
    }

    override fun assignExisitngScheduler() {
        val tmpAcctIdList = listOf("youngjai", "hwang1", "purestar", "shantf2")
        val availableAcct = tmpAcctIdList.take(threadCount)

        val perAssignedCnt = ceil(TOT_STOCK_LIST.size.toDouble() / threadCount.toDouble()).toInt()

        TOT_STOCK_LIST.chunked(perAssignedCnt).forEachIndexed { idx, subStockList ->
            val acctId: String = availableAcct[idx]
            stockAssingedMap[acctId] = subStockList
            scheduledTaskStatusMap[acctId] = scheduler.scheduleAtFixedRate({ execute(subStockList, acctId) },
                0L, 10L, TimeUnit.MILLISECONDS)
        }
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

data class PriceAt(
    val stockCd: String,
    val price: String = "default",
    val at : Long = System.currentTimeMillis()/1000,
    val priceUnit: String
)
