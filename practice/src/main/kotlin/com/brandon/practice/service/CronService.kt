package com.brandon.practice.service

interface CronService {
    fun shutDown()
    fun restartScheduler(initial: Boolean, threadCount: Int): Unit

}
