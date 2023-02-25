package com.brandon.practice.domain

data class StockPriceInfoWithType (
    val price: String,
    val priceUnit: String,
    val market: String?
)

data class DomesticPrice(
    val price: String,
    val priceUnit: String
)

data class OverseaPrice(
    val stockCd: String,
    val overseaPrice: String
)

data class OverseaStockPriceWithType(
    val stockCd: String,
    val overseaPrice: String,
    val market: String?
)
