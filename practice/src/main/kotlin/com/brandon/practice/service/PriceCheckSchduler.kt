package com.brandon.practice.service

import com.brandon.practice.service.PriceCheckService.Companion.MIXED_STOCK_SAMPLE
import com.brandon.practice.service.PriceCheckService.Companion.MIXED_STOCK_SAMPLE_V2
import com.brandon.practice.service.PriceCheckService.Companion.MIXED_STOCK_SAMPLE_V3
import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.concurrent.ExecutorService

@Component
class PriceCheckSchduler(
    private val priceCheckService: PriceCheckService
) {

    @Async
    @Scheduled(fixedDelay = 10L)
    fun priceAsync1() {
        val currentThread = Thread.currentThread()
        val threadId = currentThread.id
        val threadName = currentThread.name
        val threadState = currentThread.state
        println("hwang1 : Current thread ID: $threadId")
        println("hwang1 : Current thread name: $threadName")
        println("hwang1 : Current thread state: $threadState")
        priceCheckService.priceCollect(MIXED_STOCK_SAMPLE_V3, "hwang1")
    }

    @Async
    @Scheduled(fixedDelay = 10L)
    fun priceAsync2() {
        val currentThread = Thread.currentThread()
        val threadId = currentThread.id
        val threadName = currentThread.name
        val threadState = currentThread.state
        println("youngjai : Current thread ID: $threadId")
        println("youngjai : Current thread name: $threadName")
        println("youngjai : Current thread state: $threadState")
        priceCheckService.priceCollect(MIXED_STOCK_SAMPLE_V2, "youngjai")
    }

    @Async
    @Scheduled(fixedDelay = 10L)
    fun priceAsync3() {
        val currentThread = Thread.currentThread()
        val threadId = currentThread.id
        val threadName = currentThread.name
        val threadState = currentThread.state
        println("purestar : Current thread ID: $threadId")
        println("purestar : Current thread name: $threadName")
        println("purestar : Current thread state: $threadState")
        priceCheckService.priceCollect(MIXED_STOCK_SAMPLE, "purestar")
    }

}
