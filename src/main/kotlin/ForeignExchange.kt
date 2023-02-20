enum class ForeignExchange {

    SIMCHEON(buyTrId = "TTTS0305U", sellTrId = "TTTS0304U", exchangeCode = "SZAA"),
    HONGKONG(buyTrId = "TTTS1002U", sellTrId = "TTTS1001U", exchangeCode = "SEHK"),
    SHANGHAI(buyTrId = "TTTS0202U", sellTrId = "TTTS1005U", exchangeCode = "SHAA"),
    NASDAQ(buyTrId = "JTTT1002U", sellTrId = "JTTT1006U", exchangeCode = "NASD", alias = listOf("나스닥", "nasdaq")),
    NEWYORK(buyTrId = "JTTT1002U", sellTrId = "JTTT1006U", exchangeCode = "NYSE", alias = listOf("뉴욕", "나이스", "nyse")),
    AMEX(buyTrId = "JTTT1002U", sellTrId = "JTTT1006U", exchangeCode = "AMEX"),
    JAPAN(buyTrId = "TTTS0308U", sellTrId = "TTTS0307U", exchangeCode = "TKSE");

    val buyTrId: String
    val sellTrId: String
    val exchangeCode: String
    val alias: List<String?>


    constructor(buyTrId: String, sellTrId: String, exchangeCode: String, alias: List<String?> = emptyList()) {
        this.buyTrId = buyTrId
        this.exchangeCode = exchangeCode
        this.sellTrId = sellTrId
        this.alias = alias
    }



    companion object {
        private fun isEnglish(str: String): Boolean {
            val pattern = "^[a-zA-Z0-9 ]*\$"
            val regex = Regex(pattern)
            return regex.matches(str)
        }

        fun getExchangeByAlias(target: String): ForeignExchange {
            for(exchange in  values().filter{ e -> e.alias.isNotEmpty() }) {
                if(isEnglish(target)){
                    if(exchange.alias.contains(target.lowercase())){
                        return exchange
                    }
                } else {
                    if(exchange.alias.contains(target)){
                        return exchange
                    }
                }
            }
            throw ImProperMarketCodeExcpetion("dfasdf")
        }
    }
}

data class ImProperMarketCodeExcpetion (
    val desc: String
): RuntimeException()
