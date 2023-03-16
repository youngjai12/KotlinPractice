package com.brandon.practice.hantoo

import com.brandon.practice.client.PriceApiTemplate
import com.brandon.practice.domain.DomesticHantooPrice
import com.brandon.practice.domain.OverseaHantooPrice
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

    data class OverseaPriceRequest(
        val request: OverseaPriceRequest.Request,
        val header: OverseaPriceRequest.Header
    ): ApiTemplate<OverseaPriceRequest.Response, OverseaPriceRequest.Request, OverseaPriceRequest.Header> {
        class Response(
            @JsonProperty("output")
            val stockPriceDetail: OverseaHantooPrice.Output? = null,
            val rt_cd: String? = null,
            val msg_cd: String? = null,
            val msg1: String? = null
        ): ApiResponse, PriceResponse {
            override fun currentPrice(): String {
                if(stockPriceDetail?.last.isNullOrEmpty()){
                    return "-1"
                }else{
                    return stockPriceDetail?.last!!
                }
            }
            override fun priceUnit(): String {
                return if(currentPrice().toDouble() > 10.0) {
                    "5"
                } else {
                    "10"
                }
            }
        }

        class Request(
            @JsonProperty("AUTH")
            val auth: String = "",
            @JsonProperty("EXCD")
            val excd: String,
            @JsonProperty("SYMB")
            val symb: String
        ): ApiRequest

        class Header(
            @JsonProperty("Content-Type")
            val contentType: String = MediaType.APPLICATION_JSON_VALUE,
            val authorization: String,
//            @JsonProperty("Connection")
//            val connection: String = "close",
            val appkey: String,
            val appsecret: String,
            val tr_id: String = "HHDFS00000300"
        ): ApiHeader

        override fun path(): String = "uapi/overseas-price/v1/quotations/price"
        override fun method(): HttpMethod = HttpMethod.GET
        override fun header(): Header = header
        override fun request(): Request = request
    }

    data class PostException(
        @JsonProperty("rt_cd")
        val rtCdd: String? ,
        @JsonProperty("msg_cd")
        val msgCdd: String?,
        @JsonProperty("msg1")
        val msg22: String?
    ): RuntimeException()


}
