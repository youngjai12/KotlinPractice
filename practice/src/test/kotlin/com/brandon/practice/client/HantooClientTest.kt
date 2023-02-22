package com.brandon.practice.client

import com.brandon.practice.hantoo.HantooClient
import com.brandon.practice.hantoo.HantooPriceTemplate
import com.brandon.practice.hantoo.HantooPriceTemplate.*
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import reactor.core.publisher.Mono

@TestPropertySource(locations = ["classpath:application.yml"])
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.NONE
)
class HantooClientTest {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Autowired
    lateinit var hantooClient: HantooClient

    @Value("\${auth.appkey}")
    lateinit var appkey: String

    @Value("\${auth.appsecret}")
    lateinit var appsecret: String

    @Value("\${auth.accessToken}")
    lateinit var accessToken: String

    @Test
    fun getDomesticPriceTest() {
        logger.info("appkey: ${appkey}")
        logger.info("appsecret: ${appsecret}")
        val domesticPriceRequest = DomesticPriceRequest(
            request=DomesticPriceRequest.Request(
                fid_input_iscd = "331520"
            ),
            header = DomesticPriceRequest.Header(
                authorization = "Bearer ${accessToken}",
                appsecret = appsecret, appkey = appkey
            )
        )

        val priceMono = hantooClient.getPrice(domesticPriceRequest)
        val tmpPriceInfo: PriceResponse? = priceMono.block()
        val priceInfo = tmpPriceInfo?.currentPrice()
        val priceUnit = tmpPriceInfo?.priceUnit()

        logger.info("### ${priceInfo}, $priceUnit")


    }
}
