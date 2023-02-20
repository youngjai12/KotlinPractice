package module

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import domain.StockPriceInfo
import org.mockserver.integration.ClientAndServer
import org.mockserver.model.HttpRequest
import org.mockserver.model.HttpResponse
import org.mockserver.model.MediaType
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod

class MockWebServer {
    lateinit var mockServer: ClientAndServer
    private val logger = LoggerFactory.getLogger(javaClass)

    companion object{
        val OVERSEA_PRICE_URL = "/price/oversea"
        val DOMESTIC_PRICE_URL = "/price/domestic"
    }

    fun startServer(port: Int) {
        mockServer = ClientAndServer.startClientAndServer(port);
    }
    fun stopServer(){
        mockServer.stop()
    }

    val mapper = jacksonObjectMapper().registerKotlinModule()

    fun getCurrentPrice(stockCd: String) {

        val priceResponse = StockPriceInfo(price = "12393", priceUnit = "10")
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
}
