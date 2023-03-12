package com.brandon.practice.threadTest

import com.brandon.practice.service.ConfirmCheckService
import com.brandon.practice.service.OrderService
import com.brandon.practice.service.PriceCheckService
import com.brandon.practice.threadExp.ThreadExpService
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Future

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.NONE
)
class ThreadExpTest {

    val logger: Logger = LoggerFactory.getLogger(javaClass)

    @Autowired
    lateinit var threadExpService: ThreadExpService

    @MockBean
    lateinit var priceCheckService: PriceCheckService

    @MockBean
    lateinit var orderService: OrderService

    @MockBean
    lateinit var confirmCheckService: ConfirmCheckService

    @Test
    fun threadTest1(){
        threadExpService.initiateThread("youngjai")
        Thread.sleep(40000L)
    }

    fun monitorThreadStatus(statusMap: ConcurrentHashMap<String, Future<*>?>) {
        for (i in 0..20){
            val doneThreadCnt = statusMap.filter{ threadStatus -> threadStatus.value?.isDone!! }.size
            val canceledThreadCnt = statusMap.filter { item -> item.value?.isCancelled!! }.size
            val resultMap = statusMap.map{item -> item.value?.get()}

            logger.info("doned thread : ${doneThreadCnt}")
            logger.info("cancelled thread: ${canceledThreadCnt}")
            logger.info("${resultMap}")
            Thread.sleep(1000)
        }
    }

    @Test
    fun threadStatusCheck(){
        threadExpService.initiateThread("youngjai")
        val statusMap = threadExpService.getThreadStatus()
        monitorThreadStatus(statusMap)
        Thread.sleep(40000L)
    }

    @Test
    fun threadExceptionCheck() {
        threadExpService.exceptionOccurThread()
        logger.info(" ##### exception thread Test ####### ")
        val statusMap = threadExpService.getThreadStatus()
        for (i in 0..20){
            val doneThreadCnt = statusMap.filter{ threadStatus -> threadStatus.value?.isDone!! }.size
            val canceledThreadCnt = statusMap.filter { item -> item.value?.isCancelled!! }.size
            val resultMap = statusMap.map{item -> item.value?.get()}

            logger.info("doned thread : ${doneThreadCnt}")
            logger.info("cancelled thread: ${canceledThreadCnt}")
            logger.info("${resultMap}")
            Thread.sleep(1000)
        }
        Thread.sleep(40000L)
    }
}
