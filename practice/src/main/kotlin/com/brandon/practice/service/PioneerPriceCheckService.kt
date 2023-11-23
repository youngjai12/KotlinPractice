package com.brandon.practice.service

import com.brandon.practice.client.PioneerClient
import com.brandon.practice.client.PriceApiTemplate
import com.brandon.practice.domain.ApiResponseType
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.concurrent.ConcurrentHashMap

@Service
class PioneerPriceCheckService (
    val pioneerClient: PioneerClient
    ){

    val logger = LoggerFactory.getLogger(javaClass)
    val currentPriceInfo = ConcurrentHashMap<String, PriceAt>()

    companion object{
        val MIXED_STOCK_SAMPLE_V2 = listOf("104460", "110020", "NVDA", "140910", "AAPL", "191410", "INTL")
    }

    fun getPriceMono(stockCd: String): Mono<PriceApiTemplate.PriceResponseTemplate> {
        val priceRequestForm = if(stockCd.matches("[a-zA-Z]+".toRegex())){
            PriceApiTemplate.OverseaPriceRequest(
                request = PriceApiTemplate.OverseaPriceRequest.Request(
                    symb = stockCd
                ),
                header = PriceApiTemplate.OverseaPriceRequest.Header()
            )
        } else {
            PriceApiTemplate.DomesticPriceRequest(
                request = PriceApiTemplate.DomesticPriceRequest.Request(
                    fid_input_iscd = stockCd
                ),
                header = PriceApiTemplate.DomesticPriceRequest.Header()
            )
        }
        return pioneerClient.getPrice(priceRequestForm)
    }


    fun priceCheck(stockList: List<String>) {
        var priceTraceString = ""
        Flux.fromIterable(stockList).flatMap { stockCd ->
            val priceInfo: Mono<PriceApiTemplate.PriceResponseTemplate>
            = getPriceMono(stockCd).onErrorResume {
                when(it) {
                    is PriceApiTemplate.PostException ->
                        logger.error("Known Api Error ${it.msg22}")
                    else ->
                        logger.error("[$stockCd] got unknown error ${it.message} ${it.cause}")
                }
                when(stockCd.matches("[a-zA-Z]+".toRegex())){
                    true -> Mono.just(PriceApiTemplate.OverseaPriceRequest.Response())
                    false -> Mono.just(PriceApiTemplate.DomesticPriceRequest.Response())
                }
            }
            val stockCdMono = Mono.just(stockCd)
            Mono.zip(stockCdMono, priceInfo).map{ tuple ->
                PriceAt(stockCd=tuple.t1, price = tuple.t2.currentPrice(), priceUnit=tuple.t2.priceUnit())
            }
        }.collectList().block()?.forEach{
            priceTraceString = priceTraceString +"${it.stockCd}(${it.price})"
            logger.error("## ${it.stockCd}(${it.price})")
            if(it.price != "-1"){
                currentPriceInfo[it.stockCd] = it
            }
        }
    }

    fun priceCheckV2(stockList: List<String>) {
        var priceTraceString = ""
        Flux.fromIterable(stockList).flatMap { stockCd ->
            val priceInfo: Mono<PriceApiTemplate.PriceResponseTemplate> = getPriceMono(stockCd)
                    .onErrorResume {
                when(it) {
                    is PriceApiTemplate.PostException -> {
                        logger.error("Known Api Error ${it.msg22}")
                        if(it.msgCdd == ApiResponseType.ACCESS_TOKEN_EXPIRATION.code){
                            throw it
                        }
                    }
                    else ->
                        logger.error("[$stockCd] got unknown error ${it.message} ${it.cause}")
                }
                when(stockCd.matches("[a-zA-Z]+".toRegex())){
                    true -> {
                        logger.error("${stockCd} onErrorResume")
                        Mono.just(PriceApiTemplate.OverseaPriceRequest.Response())
                    }
                    false -> Mono.just(PriceApiTemplate.DomesticPriceRequest.Response())
                }
            }
            val stockCdMono = Mono.just(stockCd)
            Mono.zip(stockCdMono, priceInfo).map{ tuple ->
                PriceAt(stockCd=tuple.t1, price = tuple.t2.currentPrice(), priceUnit=tuple.t2.priceUnit())
            }
        }.subscribe(
            { priceInfo ->
                logger.error("Successfully got price ${priceInfo.stockCd}(${priceInfo.price})")
                currentPriceInfo[priceInfo.stockCd] = priceInfo
            },
            {error ->
                    when(error) {
                        is PriceApiTemplate.PostException -> {

                        }
                    }
                    logger.error(" This is error ${error.message} = ${error.stackTraceToString()}")
            }
        )


        logger.error("## tot-completed : getting stocks ${priceTraceString}")

    }
}
