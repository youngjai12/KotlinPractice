package config

import client.ClientProperties
import client.TestWebClient
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(ClientProperties::class)
@ComponentScan(basePackages=["client"])
@ConditionalOnProperty(value = ["client.hantoo.enable"], havingValue = "true")
class AutoClientConfiguration (
    private val clientProperties: ClientProperties
){

    @Bean
    fun testWebClient() = TestWebClient(clientProperties)
}
