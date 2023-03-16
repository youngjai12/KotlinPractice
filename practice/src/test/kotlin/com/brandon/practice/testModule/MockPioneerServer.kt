package com.brandon.practice.testModule

import com.brandon.practice.client.PriceApiTemplate
import com.brandon.practice.domain.*
import com.brandon.practice.module.PriceInfoDeserializer
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.mockserver.integration.ClientAndServer
import org.mockserver.model.HttpRequest
import org.mockserver.model.HttpResponse
import org.mockserver.model.MediaType
import org.slf4j.LoggerFactory
import org.springframework.http.HttpMethod

class MockPioneerServer {
    lateinit var mockServer: ClientAndServer
    private val logger = LoggerFactory.getLogger(javaClass)

    companion object{
        val OVERSEA_PRICE_URL = "/price/oversea"
        val OVERSEA_PRICE_URL_TYPE = "/price/oversea/type"
        val DOMESTIC_PRICE_URL = "/price/domestic"
        val DOMESTIC_PRICE_URL_TYPE = "/price/domestic/type"
        val ERROR_RAISE_STOCK_CD = listOf("123123", "456456")

    }

    fun startServer(port: Int) {
        mockServer = ClientAndServer.startClientAndServer(port);
    }
    fun stopServer(){
        mockServer.stop()
    }

    val mapper = jacksonObjectMapper().registerKotlinModule()
        .registerModule(
            SimpleModule().addDeserializer(
                PriceApiTemplate.PriceResponseTemplate::class.java,
                PriceInfoDeserializer()
            )
        )

    fun getDomesticPrice(stockCd: String) {

        val priceResponse = StockPriceInfoWithType(price = "12393", priceUnit = "10", market= "domestic")
        val responseString = mapper.writeValueAsString(priceResponse)

        mockServer.`when`(
            HttpRequest.request()
                .withMethod(HttpMethod.GET.name)
                .withPath(DOMESTIC_PRICE_URL_TYPE)
                .withQueryStringParameter("fid_input_iscd", stockCd)

        ).respond(
            HttpResponse.response()
                .withStatusCode(200)
                .withBody(responseString, MediaType.APPLICATION_JSON_UTF_8)
        )
    }

    fun raise5xxError(stockCd: String) {
        val priceResponse = DomesticPrice(price = stockCd, priceUnit = "10")
        val responseString = mapper.writeValueAsString(priceResponse)

        mockServer.`when`(
            HttpRequest.request()
                .withMethod(HttpMethod.GET.name)
                .withPath(DOMESTIC_PRICE_URL)
                .withQueryStringParameter("fid_input_iscd", stockCd)

        ).respond(
            HttpResponse.response()
                .withStatusCode(500)
        )
    }

    fun getDomesticPriceNoType(stockCd: String) {

        val priceResponse = DomesticPrice(price = "12393", priceUnit = "10")
        val responseString = mapper.writeValueAsString(priceResponse)

        mockServer.`when`(
            HttpRequest.request()
                .withMethod(HttpMethod.GET.name)
                .withPath(DOMESTIC_PRICE_URL)
                .withQueryStringParameter("fid_input_iscd", stockCd)

        ).respond(
            HttpResponse.response()
                .withStatusCode(200)
                .withBody(responseString, MediaType.APPLICATION_JSON_UTF_8)
        )
    }

    fun getOverseaPriceNoType(stockCd: String) {
        //val priceResponse = OverseaPrice(stockCd = stockCd, overseaPrice = "13.1")
        val priceResponse = PriceApiTemplate.OverseaPriceRequest.Response(stockCd=stockCd, overseaPrice = "13.1123")
        val responseString = mapper.writeValueAsString(priceResponse)
        logger.info("response string : ${responseString} 1(${responseString[1]})")
        mockServer.`when`(
            HttpRequest.request()
                .withMethod(HttpMethod.GET.name)
                .withPath(OVERSEA_PRICE_URL)
                .withQueryStringParameter("symb", stockCd)

        ).respond(
            HttpResponse.response()
                .withStatusCode(200)
                .withBody(responseString, MediaType.APPLICATION_JSON_UTF_8)
        )
    }

    fun getOverseaPrice(stockCd: String) {
        val priceResponse = OverseaStockPriceWithType(stockCd = stockCd, overseaPrice = "13.1", market= "oversea")
        val responseString = mapper.writeValueAsString(priceResponse)

        mockServer.`when`(
            HttpRequest.request()
                .withMethod(HttpMethod.GET.name)
                .withPath(OVERSEA_PRICE_URL_TYPE)
                .withQueryStringParameter("symb", stockCd)
        ).respond(
            HttpResponse.response()
                .withStatusCode(200)
                .withBody(responseString, MediaType.APPLICATION_JSON_UTF_8)
        )
    }
}
