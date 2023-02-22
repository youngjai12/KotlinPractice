package com.brandon.practice.config

import com.brandon.practice.hantoo.HantooClient
import com.brandon.practice.hantoo.HantooClientProperties
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(HantooClientProperties::class)
@ConditionalOnProperty("client.hantoo.enable", havingValue = "true")
class HantooClientConfiguration(
    private val hantooClientProperties: HantooClientProperties
) {
  @Bean
  fun HantooClient() : HantooClient = HantooClient(hantooClientProperties)
}
