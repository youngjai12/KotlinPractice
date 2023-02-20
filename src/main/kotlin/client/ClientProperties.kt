package client

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import java.time.Duration

@ConstructorBinding
@ConfigurationProperties("client.hantoo")
data class ClientProperties (
    val enable: Boolean,
    val server: String,
    val timeout: Duration = Duration.ofSeconds(12)
)
