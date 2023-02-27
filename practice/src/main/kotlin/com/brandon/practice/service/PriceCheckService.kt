package com.brandon.practice.service

import com.brandon.practice.hantoo.HantooClient
import com.brandon.practice.hantoo.HantooPriceTemplate

class PriceCheckService(
    val hantooClient: HantooClient,
) {

    val MIXED_STOCK_SAMPLE = listOf("002420", "002820", "006880", "008500",
        "MSFT", "033250", "079190", "INTC", "101400")

    val MIXED_STOCK_SAMPLE_V2 = listOf("104460", "110020", "NVDA", "140910", "AAPL", "191410", "263920")
    val MIXED_STOCK_SAMPLE_V3 = listOf("001820", "006340", "REGN", "039560", "META", "066430", "AMZN")



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
