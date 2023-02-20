package com.brandon.practice.domain

data class StockPriceInfo (
    val price: String,
    val priceUnit: String,
    val type: String
)

data class DomesticPrice(
    val price: String,
    val priceUnit: String
)

data class OverseaPrice(
    val stockCd: String,
    val overseaPrice: String
)

data class OverseaStockPrice(
    val stockCd: String,
    val overseaPrice: String,
    val type: String
)
