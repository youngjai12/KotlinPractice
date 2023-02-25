package com.brandon.practice.module

import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(UserInfoProperties::class)
class UserInfoParser(
    private val userInfoProperties: UserInfoProperties
) {
    private val logger =  LoggerFactory.getLogger(javaClass)

    // appkey 는 list 형태로 되어있는것이 아님. 그래서 Map<String, String> 형태라서
    // .get으로 원소에 access함.
    fun getAppKey(acctId: String): String? {
        logger.info("${userInfoProperties.appkey}")

        return userInfoProperties.appkey.get(acctId)
    }

    fun getAppSecret(acctId: String): String? {
        return userInfoProperties.getAppSecret(acctId)
    }
}
