package com.brandon.practice.service

interface ScheduleType {
    sealed interface QueueScheduler {
        fun execute()
    }

}
