package com.brandon.practice.hantoo

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import java.time.Duration

@ConstructorBinding
@ConfigurationProperties("client.hantoo")
class HantooClientProperties (
    val enable: Boolean,
    val server: String,
    val timeout: Duration = Duration.ofSeconds(12)
)
