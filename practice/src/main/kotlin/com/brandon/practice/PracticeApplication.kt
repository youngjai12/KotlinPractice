package com.brandon.practice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.runApplication

@SpringBootApplication
class PracticeApplication

fun main(args: Array<String>) {
	// runApplication<PracticeApplication>(*args)

	SpringApplicationBuilder(PracticeApplication::class.java)
		.properties("spring.config.location=classpath:/application.yml")
		.run(*args)
}
