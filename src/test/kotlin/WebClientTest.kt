import client.TestWebClient
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.NONE,
    properties = [
        "client.hantoo.enable=true",
        "client.hantoo.server=http://localhost:9443"
    ]
)
class WebClientTest {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Autowired
    lateinit var testClient: TestWebClient


}
