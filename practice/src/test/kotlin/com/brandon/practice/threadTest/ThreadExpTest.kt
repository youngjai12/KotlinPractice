package com.brandon.practice.threadTest

import com.brandon.practice.threadExp.ThreadExpService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.NONE
)
class ThreadExpTest {

    @Autowired
    lateinit var threadExpService: ThreadExpService

    @Test
    fun threadTest1(){
        Thread.sleep(200000L)
        threadExpService.initiateThread("youngjai")
    }
}
