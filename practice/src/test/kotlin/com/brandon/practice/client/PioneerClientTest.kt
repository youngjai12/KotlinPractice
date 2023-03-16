package com.brandon.practice.client

import com.brandon.practice.client.PioneerClient
import com.brandon.practice.client.PriceApiTemplate
import com.brandon.practice.service.ConfirmCheckService
import com.brandon.practice.service.OrderService
import com.brandon.practice.service.PriceCheckService
import com.brandon.practice.testModule.MockPioneerServer
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.NONE,
    properties = [
        "client.pioneer.enable=true",
        "client.pioneer.server=http://localhost:9443",
        "client.pioneer.timeout=3s"
    ]
)
class PioneerClientTest {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Autowired
    lateinit var pioneerClient: PioneerClient

    private val mockPioneerServer: MockPioneerServer = MockPioneerServer()

    @MockBean
    lateinit var priceCheckService: PriceCheckService

    @MockBean
    lateinit var orderService: OrderService

    @MockBean
    lateinit var confirmCheckService: ConfirmCheckService

    @BeforeEach
    fun beforeEach() {
        mockPioneerServer.startServer(port=9443)
        logger.info("####### initialized server ###")
    }


    @Test
    fun priceRequestExceptionTest() {
        for (stockCd in MockPioneerServer.ERROR_RAISE_STOCK_CD){
            mockPioneerServer.raise5xxError("123123")
        }
        val domesticPriceRequest = PriceApiTemplate.DomesticPriceRequest(
            request = PriceApiTemplate.DomesticPriceRequest.Request(
                fid_input_iscd = "123123"
            ),
            header = PriceApiTemplate.DomesticPriceRequest.Header()
        )

        val priceMono = pioneerClient.getPriceException(domesticPriceRequest)
        val tmpPriceInfo: PriceApiTemplate.PriceResponseTemplate? = priceMono.block()
        val priceInfo =tmpPriceInfo?.currentPrice()
        val priceUnit = tmpPriceInfo?.priceUnit()

        logger.info("### ${priceInfo}, $priceUnit")
    }

    @Test
    fun getDomesticPriceTest() {
        val stockCd: String = "AAPL"
        mockPioneerServer.getOverseaPrice(stockCd)
        mockPioneerServer.getDomesticPrice("12393")

        val overseaPriceRequest = PriceApiTemplate.OverseaPriceRequest(
            request = PriceApiTemplate.OverseaPriceRequest.Request(
                symb = stockCd
            ),
            header = PriceApiTemplate.OverseaPriceRequest.Header()
        )

        val domesticPriceRequest = PriceApiTemplate.DomesticPriceRequest(
            request = PriceApiTemplate.DomesticPriceRequest.Request(
                fid_input_iscd = "12393"
            ),
            header = PriceApiTemplate.DomesticPriceRequest.Header()
        )

        runCatching {
            val priceMono = pioneerClient.getPrice(overseaPriceRequest)
            val tmpPriceInfo = priceMono.block()!!
            val priceInfo =tmpPriceInfo.currentPrice()
            val priceUnit = tmpPriceInfo.priceUnit()
            logger.info("### ${priceInfo}, $priceUnit")

            val domesticPriceMono = pioneerClient.getPrice(domesticPriceRequest)
            val tmpPriceInfo2 = domesticPriceMono.block()!!
            val priceInfo2 =tmpPriceInfo2.currentPrice()
            val priceUnit2 = tmpPriceInfo2.priceUnit()
            logger.info("### domestic ${priceInfo2}, $priceUnit2")
        }.recover {
            when(it) {
                is PriceApiTemplate.PostException ->
                    logger.error("msg : ${it.msg22}" )
                else ->
                    logger.info("nothing ${it.message}")
            }

        }

    }

    @Test
    fun typeSpecifiedTest() {
        val stockCd = "AAPL"
        mockPioneerServer.getOverseaPriceNoType(stockCd)
        //mockPioneerServer.getDomesticPriceNoType("12393")

        val overseaPriceRequest = PriceApiTemplate.OverseaPriceRequest(
            request = PriceApiTemplate.OverseaPriceRequest.Request(
                symb = stockCd
            ),
            header = PriceApiTemplate.OverseaPriceRequest.Header()
        )

        runCatching {
            val priceMono = pioneerClient.getOverSeaPrice(overseaPriceRequest)
            val tmpPriceInfo = priceMono.block()!!
            val priceInfo =tmpPriceInfo.currentPrice()
            val priceUnit = tmpPriceInfo.priceUnit()
            logger.info("### ${priceInfo}, $priceUnit")

        }.recover {
            when(it) {
                is PriceApiTemplate.PostException ->
                    logger.error("msg : ${it.msg22}" )
                else ->
                    logger.info("nothing ${it.message}")
            }
        }
    }

    @Test
    fun monoSubscriptionTest() {
        val stockCd = "AAPL"
        mockPioneerServer.getOverseaPriceNoType(stockCd)
        mockPioneerServer.getDomesticPriceNoType("12393")
        val overseaPriceRequest = PriceApiTemplate.OverseaPriceRequest(
            request = PriceApiTemplate.OverseaPriceRequest.Request(
                symb = stockCd
            ),
            header = PriceApiTemplate.OverseaPriceRequest.Header()
        )
        val domesticPriceRequest = PriceApiTemplate.DomesticPriceRequest(
            request = PriceApiTemplate.DomesticPriceRequest.Request(
                fid_input_iscd = "12393"
            ),
            header = PriceApiTemplate.DomesticPriceRequest.Header()
        )
        val overseaPriceMono = pioneerClient.getPrice(overseaPriceRequest)
        overseaPriceMono.subscribe {response ->
            when(response) {
                is PriceApiTemplate.OverseaPriceRequest.Response -> {
                    logger.info("#### oversea ##### stockCd(${response.stockCd}) price(${response.overseaPrice}) ")
                }
                is PriceApiTemplate.DomesticPriceRequest.Response -> {
                    logger.info("## domestic ## : price(${response.price}) priceUnit(${response.priceUnit})")
                }

            }
        }


    }

    @Test
    fun customizedSerializeTest() {
        val stockCd = "AAPL"
        mockPioneerServer.getOverseaPriceNoType(stockCd)
        mockPioneerServer.getDomesticPriceNoType("12393")

        val overseaPriceRequest = PriceApiTemplate.OverseaPriceRequest(
            request = PriceApiTemplate.OverseaPriceRequest.Request(
                symb = stockCd
            ),
            header = PriceApiTemplate.OverseaPriceRequest.Header()
        )

        val domesticPriceRequest = PriceApiTemplate.DomesticPriceRequest(
            request = PriceApiTemplate.DomesticPriceRequest.Request(
                fid_input_iscd = "12393"
            ),
            header = PriceApiTemplate.DomesticPriceRequest.Header()
        )

        runCatching {
            val priceMono = pioneerClient.getPrice(overseaPriceRequest)
            val tmpPriceInfo = priceMono.block()!!
            val priceInfo =tmpPriceInfo.currentPrice()
            val priceUnit = tmpPriceInfo.priceUnit()
            logger.info("### ${priceInfo}, $priceUnit")

            val domesticPriceMono = pioneerClient.getPrice(domesticPriceRequest)
            val tmpPriceInfo2 = domesticPriceMono.block()!!
            val priceInfo2 =tmpPriceInfo2.currentPrice()
            val priceUnit2 = tmpPriceInfo2.priceUnit()
            logger.info("### domestic ${priceInfo2}, $priceUnit2")




        }.recover {
                when(it) {
                    is PriceApiTemplate.PostException ->
                        logger.error("msg : ${it.msg22}" )
                    else ->
                        logger.info("nothing ${it.message}")
                }
        }
    }

}
