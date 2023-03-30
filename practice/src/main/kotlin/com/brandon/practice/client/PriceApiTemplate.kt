package com.brandon.practice.client

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType

interface PriceApiTemplate {
//    @JsonTypeInfo(
//        use = JsonTypeInfo.Id.NAME,
//        include = JsonTypeInfo.As.PROPERTY,
//        property = "type")
//    @JsonSubTypes(
//        JsonSubTypes.Type(value = DomesticPriceRequest.Response::class, name = "domestic"),
//        JsonSubTypes.Type(value = OverseaPriceRequest.Response::class, name = "oversea")
//    )
    interface PriceResponseTemplate{
        fun currentPrice(): String
        fun priceUnit(): String
    }


    sealed interface ApiResponse
    sealed interface ApiHeader
    sealed interface ApiRequestBody

    sealed interface ApiTemplate<Res : ApiResponse, Req: ApiRequestBody, H: ApiHeader> {
        fun path(): String
        fun method(): HttpMethod
        fun header(): H
        fun request(): Req
    }

    data class  DomesticPriceRequest(
        val request: Request,
        val header: Header
    ): ApiTemplate<DomesticPriceRequest.Response, DomesticPriceRequest.Request, DomesticPriceRequest.Header> {

        class Response(
            val price: String? = null,
            val priceUnit: String? = null
        ): ApiResponse, PriceResponseTemplate {
            override fun currentPrice() = price ?: "-1"
            override fun priceUnit(): String {
                return price?.let {
                    if(price.toDouble() > 10.0){
                        "5"
                    } else {
                        "10"
                    }
                } ?: "-1"
            }
        }

        class Request(
            val fid_input_iscd: String,
        ): ApiRequestBody

        class Header(
            val contentType: String = MediaType.APPLICATION_JSON_VALUE,
//            @JsonProperty("Connection")
//            val connection: String = "close",
        ): ApiHeader

        override fun path(): String = "/price/domestic"
        override fun method(): HttpMethod = HttpMethod.GET
        override fun header(): Header = header
        override fun request() = request

    }

    data class  OverseaPriceRequest(
        val request: Request,
        val header: Header
    ): ApiTemplate<OverseaPriceRequest.Response, OverseaPriceRequest.Request, OverseaPriceRequest.Header> {

        class Response(
            val overseaPrice: String?=null,
            val priceUnit: String?=null
        ): ApiResponse, PriceResponseTemplate {
            override fun currentPrice() = overseaPrice ?: "-1"
            override fun priceUnit(): String {
                return overseaPrice?.let {
                    if(overseaPrice.toDouble() > 10.0){
                        "5"
                    } else {
                        "10"
                    }
                } ?: "-1"
            }
        }

        class Request(
            @JsonProperty("symb")
            val symb: String
        ): ApiRequestBody

        class Header(
            val contentType: String = MediaType.APPLICATION_JSON_VALUE,
//            @JsonProperty("Connection")
//            val connection: String = "close",
        ): ApiHeader

        override fun path(): String = "/price/oversea"
        override fun method(): HttpMethod = HttpMethod.GET
        override fun header(): Header =header
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
