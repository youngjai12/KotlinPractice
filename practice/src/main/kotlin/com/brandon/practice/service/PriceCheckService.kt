package com.brandon.practice.service

import com.brandon.practice.hantoo.HantooClient
import com.brandon.practice.hantoo.HantooPriceTemplate
import com.brandon.practice.module.UserInfoProperties
import org.slf4j.Logger
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
) {
    val logger: Logger = LoggerFactory.getLogger(javaClass)
    private val currentPriceInfo = ConcurrentHashMap<String, PriceAt>()
    private val stockAssingedMap = HashMap<String, List<String>>()
    private val stockAssingedMapV2 = HashMap<String, List<Stock>>()

    private val scheduledTaskStatusMap = HashMap<String, ScheduledFuture<*>?>()

    var scheduler: ScheduledExecutorService = priceMonitorscheduler

    companion object{
        val THREAD_COUNT = 4
        val CHUNK_SIZE = 15
   }

    fun execute(stockList: List<Stock>, acctId: String) {
        try {
            task(stockList, acctId)
        } catch (e: Exception){
            logger.error("error happend! ${e.message} ${e.stackTraceToString()}")
        }
    }

    fun getPriceInfoByStock(stock: Stock): PriceAt? {
        return currentPriceInfo[stock.stockCd]
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
                val priceInfo: Mono<HantooPriceTemplate.PriceResponse> =
                 getPriceMono(stockCd.stockCd, acctId, stockCd.marketCode).onErrorResume {
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

    fun task(stockList: List<Stock>, acctId: String) {
        stockList.chunked(CHUNK_SIZE).forEach{ chunkedStockList ->
            Thread.sleep(970)
            var priceTraceString = ""
            Flux.fromIterable(chunkedStockList).flatMap { stockCd ->
                val priceInfo: Mono<HantooPriceTemplate.PriceResponse> =
                    getPriceMono(stockCd.stockCd, acctId, stockCd.marketCode).onErrorResume {
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
            }.subscribe(
                { result ->
                    priceTraceString = priceTraceString +"${result.stockCd}(${result.price}) "
                    if(result.price != "-1"){
                        currentPriceInfo[result.stockCd] = result
                    }
                },
                { error -> logger.error("${error.message} ${error.stackTrace}")}
            )
            logger.info("inside priceCheck[${acctId}] - ${priceTraceString}")
        }
    }

    fun execute33(stockList: List<String>, acctId: String) {
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
