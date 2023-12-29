package com.brandon.stocksocket

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class StocksocketApplication

fun main(args: Array<String>) {
	runApplication<StocksocketApplication>(*args)
}
