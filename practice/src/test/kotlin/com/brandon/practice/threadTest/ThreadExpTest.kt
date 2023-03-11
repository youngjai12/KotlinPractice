package com.brandon.practice.threadTest

import com.brandon.practice.service.ConfirmCheckService
import com.brandon.practice.service.OrderService
import com.brandon.practice.service.PriceCheckService
import com.brandon.practice.threadExp.ThreadExpService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.NONE
)
class ThreadExpTest {

    @Autowired
    lateinit var threadExpService: ThreadExpService

    @MockBean
    lateinit var priceCheckService: PriceCheckService

    @MockBean
    lateinit var orderService: OrderService

    @Test
    fun threadTest1(){
        threadExpService.initiateThread("youngjai")
        Thread.sleep(20000L)
    }
}
