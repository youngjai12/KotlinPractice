package com.brandon.practice.service

import com.brandon.practice.client.PioneerClient
import com.brandon.practice.client.PriceApiTemplate
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
        val MIXED_STOCK_SAMPLE_V2 = listOf("104460", "110020", "NVDA", "140910", "AAPL", "191410", "263920")
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
                    fid_input_iscd = "12393"
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
            priceTraceString = priceTraceString +"${it.stockCd}(${it.price}) "
            currentPriceInfo[it.stockCd] = it
        }
    }
}
