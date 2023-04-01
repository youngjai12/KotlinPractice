package com.brandon.practice.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.ApplicationContext
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
): ThreadPoolManager {
    override lateinit var threadPool: ScheduledExecutorService
    override val logger: Logger = LoggerFactory.getLogger(javaClass)
    override val POOL_SIZE: Int = 2

    override val threadStatusMap = HashMap<String, ScheduledFuture<*>?>()
    private val classNameBeanMap = HashMap<String, ScheduleType.QueueScheduler>()

    // sealedInterface를 상속하는 subclass들에 접근해서 그 subclass의 이름을 구하는 것
    private fun getSubclassNameMap(): Map<String, Class<*>> {
         val classNameMap =  ScheduleType.QueueScheduler::class.sealedSubclasses
            .associate {
                val subClassType = ResolvableType.forClass(it.java).resolve()!!
                it.simpleName!! to subClassType
            }
        return classNameMap
    }

    init{
        threadPool = queueExecuteThreadPool
        assignThread()
    }

    override fun reAssignThreadOnPool(newThreadPool: ScheduledExecutorService) {
        threadPool = newThreadPool
        logger.info("on reAssignThread : ${newThreadPool}")
        assignThread()
    }

    override fun closeOpenedThread() {
        threadStatusMap.values.toList().forEach{
            it?.cancel(true)
        }
        threadStatusMap.clear()
    }

    final fun assignThread() {
        val instances: Sequence<ScheduleType.QueueScheduler> =
            applicationContext.getBeansOfType(ScheduleType.QueueScheduler::class.java)
            .values.asSequence()

        instances.forEach {
            classNameBeanMap[it.javaClass.simpleName] = it
            openThread(it)
        }
    }

    // controller 공개용
    fun openThread(serviceName: String) {
        val serviceCode = QueueScheduleServiceCode.getServiceByAlias(serviceName)

        return classNameBeanMap[serviceCode.className]?.let {
            openThread(it)
        }?: throw IllegalArgumentException("Invalid service name: $serviceName")
    }

    private fun openThread(serviceInstance: ScheduleType.QueueScheduler){
        val className = serviceInstance.javaClass.simpleName
        logger.info("insideEachThread pool: ${threadPool}}")
        threadStatusMap.getOrPut(className){
            threadPool.scheduleAtFixedRate({ serviceInstance.execute() },
                0L, 3000L,  TimeUnit.MILLISECONDS)
        }
    }

    fun closeThread(serviceName: String): Boolean {
        val serviceCode = QueueScheduleServiceCode.getServiceByAlias(serviceName)
        return threadStatusMap[serviceCode.className]?.let {
            it.cancel(true)
            threadStatusMap.remove(serviceCode.className)
            true
        } ?: false
    }
}

// 각 Enum의 값은 sub-class의 simpleName이 되도록 설정한다.
// Enum이 존재하는 이유는 controller 단에서 사용자의 입력을 제어하기 위함이다.
enum class QueueScheduleServiceCode {
    ORDER(className="OrderService", alias = listOf("orderservice", "order")),
    CONFIRM(className="ConfirmCheckService", alias = listOf("confirmservice", "confirm", "confirmcheckservice"))
    ;

    val alias: List<String>
    val className: String

    constructor(className: String, alias: List<String>){
        this.alias = alias
        this.className =className
    }

    companion object {
        fun getServiceByAlias(name: String): QueueScheduleServiceCode {
            return values().find{ serviceName ->
                serviceName.alias.contains(name.trim().lowercase())
            } ?: throw IllegalArgumentException("Invalid service name: $name")
        }
    }
}
