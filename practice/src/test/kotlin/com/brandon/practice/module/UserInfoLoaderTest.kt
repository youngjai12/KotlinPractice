package com.brandon.practice.module

import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource

@TestPropertySource(locations = ["classpath:application.yml"])
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.NONE
)
class UserInfoLoaderTest {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Autowired
    lateinit var userInfoParser: UserInfoParser

    @Test
    fun dictTypeValueLoadTest() {
        listOf("youngjai", "purestar").forEach { eachKey ->
            val appkey = userInfoParser.getAppKey(eachKey)
            logger.info("${eachKey} appkey(${appkey})")
        }
    }

    @Test
    fun listTypeValueLoadTest() {
        listOf("youngjai", "purestar").forEach { eachKey ->
            val appkey = userInfoParser.getAppSecret(eachKey)
            logger.info("${eachKey} appsecret(${appkey})")
        }
    }
}
