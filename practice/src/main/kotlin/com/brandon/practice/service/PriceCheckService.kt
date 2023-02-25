package com.brandon.practice.service

import com.brandon.practice.hantoo.HantooClient
import com.brandon.practice.hantoo.HantooPriceTemplate

class PriceCheckService(
    val hantooClient: HantooClient,
) {

    val MIXED_STOCK_SAMPLE = listOf("002420", "002820", "006880", "008500",
        "021040", "033250", "066790", "079190", "083660", "101400",
       "104460", "110020", "134380", "140910", "AAPL", "191410", "263920")


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
