package com.brandon.practice.service

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.ApplicationContext
import org.springframework.context.support.AbstractApplicationContext
import org.springframework.core.ResolvableType
import org.springframework.stereotype.Service
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

@Service
class QueueScheduleManager(
    @Qualifier("queueExecuteScheduler")
    private var queueExecuteThreadPool: ScheduledExecutorService,
    private val applicationContext: ApplicationContext
) {
    val threadStatusMap = HashMap<String, ScheduledFuture<*>?>()

    // sealedInterface를 상속하는 subclass들에 접근해서 그 subclass의 이름을 구하는 것
    fun getSubclassNameMap(): Map<String, Class<*>> {
         val classNameMap =  ScheduleType.QueueScheduler::class.sealedSubclasses
            .associate {
                val subClassType = ResolvableType.forClass(it.java).resolve()!!
                it.simpleName!! to subClassType
            }
        return classNameMap
    }

    init{
        assignThread()
    }

    fun assignThread() {
        val instances: Sequence<ScheduleType.QueueScheduler> =
            applicationContext.getBeansOfType(ScheduleType.QueueScheduler::class.java)
            .values.asSequence()

        for(instance in instances) {
            val name = instance.javaClass.simpleName
            threadStatusMap[name] = queueExecuteThreadPool.scheduleAtFixedRate({ instance.execute() },
                0L, 3000L,  TimeUnit.MILLISECONDS)
        }
    }

    fun closeThread(serviceName: String): Boolean {
        val service = QueueScheduleServiceName.getServiceByAlias(serviceName)
        return threadStatusMap[service.name]?.let {
            it.cancel(true)
            threadStatusMap[service.name] = null
            true
        } ?: false
    }
}

enum class QueueScheduleServiceName {
    ORDER(alias = listOf("orderservice", "order")),
    CONFIRM(alias = listOf("confirmservice", "confirm"))
    ;

    val alias: List<String>

    constructor(alias: List<String>){
        this.alias = alias
    }

    companion object {
        fun getServiceByAlias(name: String): QueueScheduleServiceName {
            return values().find{ serviceName ->
                serviceName.alias.contains(name.trim().lowercase())
            } ?: throw IllegalArgumentException("Invalid service name: $name")
        }
    }
}
