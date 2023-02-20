import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder

@SpringBootApplication
class TestApplication


fun main(args: Array<String>) {

    SpringApplicationBuilder(TestApplication::class.java)
        .properties("spring.config.location=classpath:/application.yml")
        .run(*args)

}
