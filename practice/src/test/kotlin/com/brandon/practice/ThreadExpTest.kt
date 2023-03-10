package com.brandon.practice

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
        threadExpService.initiateThread("youngjai")
    }
}
