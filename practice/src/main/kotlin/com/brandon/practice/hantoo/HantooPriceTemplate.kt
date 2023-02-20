package com.brandon.practice.hantoo

import com.brandon.practice.domain.DomesticHantooPrice
import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType

interface HantooPriceTemplate {

    interface PriceResponse {
        fun currentPrice(): String
        fun priceUnit(): String
    }

    sealed interface ApiResponse
    sealed interface ApiHeader
    sealed interface ApiRequest

    sealed interface ApiTemplate<Res: ApiResponse, Req: ApiRequest, H: ApiHeader> {
        fun path(): String
        fun method(): HttpMethod
        fun header(): H
        fun request(): Req
    }

    data class DomesticPriceRequest(
        val request: Request,
        val header: Header
    ): ApiTemplate<DomesticPriceRequest.Response, DomesticPriceRequest.Request, DomesticPriceRequest.Header> {
        class Response(
            @JsonProperty("output")
            val stockPriceDetail: DomesticHantooPrice.Output? = null,
            val rt_cd: String? = null,
            val msg_cd: String? = null,
            val msg: String? = null
        ): ApiResponse, PriceResponse {
            override fun currentPrice(): String = stockPriceDetail?.stckPrpr?: "-1"
            override fun priceUnit(): String = stockPriceDetail?.asprUnit  ?: "1"
        }

        class Request(
            val fid_input_iscd: String,
            val fid_cond_mrkt_div_code: String = "J",
            val fid_org_adj_prc: String = "1",
            val fid_period_div_code: String = "D"
        ): ApiRequest

        class Header(
            @JsonProperty("Content-Type")
            val contentType: String = MediaType.APPLICATION_JSON_VALUE,
            val authorization: String,
//            @JsonProperty("Connection")
//            val connection: String = "close",
            val appkey: String,
            val appsecret: String,
            val tr_id: String = "FHKST01010100"
        ) : ApiHeader

        override fun path(): String  = "/uapi/domestic-stock/v1/quotations/inquire-price"

        override fun method(): HttpMethod = HttpMethod.GET

        override fun header(): Header = header

        override fun request(): Request = request
    }


}
