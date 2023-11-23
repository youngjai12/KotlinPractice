package com.brandon.practice.config

import com.brandon.practice.repository.*
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.core.io.ClassPathResource


@Configuration
class MemberRepositoryConfiguration(
    private val acctInfoRepository: AcctInfoRepository,
    private val yamlAuthYamlParser: AuthYamlParser
) {


    @Profile("local")
    @Bean
    fun testMemberRepository(): UserAccessInfoRepository {
        return YamlUserAccessInfoRepository(yamlAuthYamlParser)
    }

    @Profile("prod")
    @Bean
    fun rdbMemberRepository(): UserAccessInfoRepository {
        return RdbUserAccessInfoRepository(acctInfoRepository)
    }
}