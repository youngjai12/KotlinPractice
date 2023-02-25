package com.brandon.practice.config

import com.brandon.practice.client.ClientProperties
import com.brandon.practice.client.PioneerClient
import com.brandon.practice.hantoo.HantooClient
import com.brandon.practice.hantoo.HantooClientProperties
import com.brandon.practice.subtypeDeserialize.PioneerClientV2
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(ClientProperties::class)
@ConditionalOnProperty("client.pioneer.enable", havingValue = "true")
class PioneerClientConfiguration(
    private val clientProperties: ClientProperties,
) {
    @Bean
    fun PioneerClient(): PioneerClient = PioneerClient(clientProperties)

    @Bean
    fun PioneerClientV2(): PioneerClientV2 = PioneerClientV2(clientProperties)
}
