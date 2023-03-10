package com.brandon.practice.threadTest

import com.brandon.practice.service.ConfirmCheckService
import com.brandon.practice.service.PriceCheckService
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.TestPropertySource

@TestPropertySource(locations = ["classpath:application.yml"])
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.NONE
)
class ScheduledThreadTest {

    private val logger = LoggerFactory.getLogger(javaClass)

    @Autowired
    lateinit var confirmCheckService: ConfirmCheckService

    @MockBean
    lateinit var priceCheckService: PriceCheckService


    @Test
    fun scheduledThreadTest1(){

        // 이 class가 init되면 scheduler가 당연히 할당되니깐..!
        // confirmCheckService.reassignSchedule(confirmCheckService.scheduler)
        Thread.sleep(20000L)
    }

}
