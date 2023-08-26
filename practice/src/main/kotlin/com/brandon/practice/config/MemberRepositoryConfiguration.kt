package com.brandon.practice.config

import com.brandon.practice.repository.AcctInfoRepository
import com.brandon.practice.repository.RdbUserAccessInfoRepository
import com.brandon.practice.repository.UserAccessInfoRepository
import com.brandon.practice.repository.YamlUserAccessInfoRepository
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.core.io.ClassPathResource


@Configuration
class MemberRepositoryConfiguration(
    private val acctInfoRepository: AcctInfoRepository
) {

    @Bean
    @Profile("local")
    @ConfigurationProperties(prefix = "app")
    fun appProperties(): YamlPropertiesFactoryBean {
        val factory = YamlPropertiesFactoryBean()
        factory.setResources(ClassPathResource("application-local.yml"))
        return factory
    }

    @Bean
    @Profile("local")
    fun testMemberRepository(): UserAccessInfoRepository {
        return YamlUserAccessInfoRepository()
    }

    @Bean
    @Profile("prod")
    fun rdbMemberRepository(): UserAccessInfoRepository {
        return RdbUserAccessInfoRepository(acctInfoRepository)
    }
}