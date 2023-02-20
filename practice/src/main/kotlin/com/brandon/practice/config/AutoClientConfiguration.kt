package com.brandon.practice.config

import com.brandon.practice.client.ClientProperties
import com.brandon.practice.client.PioneerClient
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(ClientProperties::class)
@ConditionalOnProperty(value = ["client.pioneer.enable"], havingValue = "true")
class AutoClientConfiguration(
    private val clientProperties: ClientProperties
) {
    @Bean
    fun PioneerClient(): PioneerClient = PioneerClient(clientProperties)
}
