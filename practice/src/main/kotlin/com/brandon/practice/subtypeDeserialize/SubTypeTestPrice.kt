package com.brandon.practice.subtypeDeserialize

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType


// JsonTypeInfo 를 통해서 별도의 deserializer를 만들지 않고도 할 수 있다.
// 다만, response 객체에 subclass가 같은 interface여도, 서로 다름을 구분할 수 있는 field가 존재햐아함
interface SubTypeTestPrice {

    // PriceResponseTemplate 이 상위의 interface이고, 이 하위에 이 interface를 extend하는 클래스들이 있다.
    @JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
    @JsonSubTypes(
        JsonSubTypes.Type(value = DomesticPriceRequest.Response::class, name = "domestic"),
        JsonSubTypes.Type(value = OverseaPriceRequest.Response::class, name = "oversea")
    )
    interface PriceResponseTemplate{
        fun currentPrice(): String
        fun priceUnit(): String
    }

    sealed interface ApiResponse
    sealed interface ApiHeader
    sealed interface ApiRequest

    sealed interface ApiTemplate<Res : ApiResponse, Req: ApiRequest, H: ApiHeader> {
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
            val price: String,
            val priceUnit: String,
            val type: String?  // 아래 class와 비교시, 구분가능한 field임.
        ): ApiResponse, PriceResponseTemplate {
            override fun currentPrice() = price
            override fun priceUnit() = priceUnit
        }

        class Request(
            val fid_input_iscd: String,
        ): ApiRequest

        class Header(
            val contentType: String = MediaType.APPLICATION_JSON_VALUE,
            @JsonProperty("Connection")
            val connection: String = "close",
        ): ApiHeader

        override fun path(): String = "/price/domestic/type"
        override fun method(): HttpMethod = HttpMethod.GET
        override fun header(): Header = header
        override fun request() = request

    }

    data class OverseaPriceRequest(
        val request: Request,
        val header: Header
    ): ApiTemplate<OverseaPriceRequest.Response, OverseaPriceRequest.Request, OverseaPriceRequest.Header> {

        class Response(
            val stockCd: String,
            val overseaPrice: String,
            val type: String?  // 아래 class와 비교시, 구분가능한 field임.
        ): ApiResponse, PriceResponseTemplate {
            override fun currentPrice() = overseaPrice
            override fun priceUnit(): String {
                return if(overseaPrice.toDouble() > 10.0) {
                    "5"
                } else {
                    "10"
                }
            }
        }

        class Request(
            val symb: String
        ): ApiRequest

        class Header(
            val contentType: String = MediaType.APPLICATION_JSON_VALUE,
            @JsonProperty("Connection")
            val connection: String = "close",
        ): ApiHeader

        override fun path(): String = "/price/oversea/type"
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
