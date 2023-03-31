package com.brandon.practice.threadTest

import com.brandon.practice.service.ConfirmCheckService
import com.brandon.practice.service.OrderService
import com.brandon.practice.service.PriceCheckService
import com.brandon.practice.service.QueueScheduleManager
import com.brandon.practice.threadExp.ThreadExpService
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.NONE
)
class ScheduleManagerTest {
    val logger: Logger = LoggerFactory.getLogger(javaClass)

    @MockBean
    lateinit var priceCheckService: PriceCheckService

    @MockBean
    lateinit var orderService: OrderService

    @MockBean
    lateinit var confirmCheckService: ConfirmCheckService

    @Autowired
    lateinit var scheduleManager: QueueScheduleManager

    @Test()
    fun scheduleNameExtractTest() {
        val output: Map<String, Class<*>> = scheduleManager.getSubclassNameMap()
        logger.info("subclass : ${output}")
        logger.info("${output["OrderService"]}")
    }
}
