package com.brandon.practice.client

import com.brandon.practice.subtypeDeserialize.PioneerClientV2
import com.brandon.practice.subtypeDeserialize.SubTypeTestPrice
import com.brandon.practice.testModule.MockPioneerServer
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest


@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.NONE,
    properties = [
        "client.pioneer.enable=true",
        "client.pioneer.server=http://localhost:9443",
        "client.pioneer.timeout=20s"
    ]
)
class SubTypeDeserializeTest {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Autowired
    lateinit var pioneerClientV2: PioneerClientV2

    private val mockPioneerServer: MockPioneerServer = MockPioneerServer()

    @BeforeEach
    fun beforeEach() {
        mockPioneerServer.startServer(port=9443)
        logger.info("####### initialized server ###")
    }

    @Test
    fun priceTest() {
        val domesticStockCd = "336570"
        val overseaStockCd = "AAPL"
        mockPioneerServer.getOverseaPrice(stockCd = overseaStockCd)
        mockPioneerServer.getDomesticPrice(stockCd = domesticStockCd)

        listOf(domesticStockCd, overseaStockCd).forEach { stockCd ->
            val priceReqForm = if(stockCd.matches("[a-zA-Z]+".toRegex())){
               SubTypeTestPrice.OverseaPriceRequest(
                    request = SubTypeTestPrice.OverseaPriceRequest.Request(
                        symb = "AAPL"
                    ),
                    header = SubTypeTestPrice.OverseaPriceRequest.Header()
                )
            } else {
                SubTypeTestPrice.DomesticPriceRequest(
                    request = SubTypeTestPrice.DomesticPriceRequest.Request(
                        fid_input_iscd = stockCd
                    ),
                    header = SubTypeTestPrice.DomesticPriceRequest.Header()
                )
            }
            val priceMono = pioneerClientV2.getPrice(priceReqForm)
            val tmpPriceInfo = priceMono.block()!!
            val priceInfo =tmpPriceInfo.currentPrice()
            val priceUnit = tmpPriceInfo.priceUnit()
            logger.info("### ${priceInfo}, $priceUnit")
        }

    }
}
