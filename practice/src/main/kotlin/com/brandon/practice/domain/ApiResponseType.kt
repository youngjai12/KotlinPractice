package com.brandon.practice.domain

enum class ApiResponseType {
    ACCESS_TOKEN_EXPIRATION(code = "EGW00123", msgFromServer = "기간이 만료된 token 입니다."),
    OUT_OF_MARKET_TIME(code = "APBK0919"),
    EXCEED_PRICE_UPPER_LIMIT(code = "APBK0406", msgFromServer = "주문가격이 상한가를 초과합니다"),
    EXCEED_BUYABLE_PRICE(code = "APBK0952", msgFromServer = "주문가능금액을 초과 했습니다"),
    SUCCESSFUL_ORDER_SEND(code = "APBK0013", msgFromServer = "주문 전송 완료 되었습니다."),
    SUCCESSFUL_ORDER_CONFIRM(code = "KIOK0510", msgFromServer = "체결조회완료"),
    BUY_CHECK_PRODUCT_NUMBER(code = "IGW00017", msgFromServer = "상품번호를 확인해주세요."),
    ORDER_TPS_LIMIT_EXCEED(code = "EGW00201", msgFromServer = "초당 거래건수를 초과하였습니다"),
    DATA_ERROR_OCCUR(code="APBK1630", msgFromServer = "데이터 오류발생입니다. -1-ORA-00001: unique constraint"),
    TRADE_SUSPENDED(code="APBK0066", msgFromServer = "거래정지종목(주식)은 취소주문만 가능(정정불가)합니다.")
    ;

    val code: String
    val msgFromServer: String?

    constructor(code: String, msgFromServer: String? = null) {
        this.code = code
        this.msgFromServer = msgFromServer
    }
}
