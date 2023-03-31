package com.brandon.practice.service

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import java.util.concurrent.ScheduledExecutorService

@Service
class GeneralTaskScheduleService(
    @Qualifier("queueExecuteScheduler")
    private var generalScheduler: ScheduledExecutorService
) {
    lateinit var scheduler: ScheduledExecutorService

    init {
        scheduler = generalScheduler  // 앱 처음시작때는 등록한 bean으로 DI받는다.
        startScheduler()
    }

    fun startScheduler() {

    }
}
