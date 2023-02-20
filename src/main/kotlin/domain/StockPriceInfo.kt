package domain

data class StockPriceInfo(
    val price: String,
    val priceUnit: String
)

data class OverseaStockPrice(
    val stockCd: String,
    val overseaPrice: String
)
