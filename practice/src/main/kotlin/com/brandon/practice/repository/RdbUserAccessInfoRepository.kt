package com.brandon.practice.repository

import java.util.concurrent.ConcurrentHashMap
import com.brandon.practice.repository.UserAccessInfoRepository.*
import org.slf4j.LoggerFactory


class RdbUserAccessInfoRepository(
    private val acctInfoRepository: AcctInfoRepository
): UserAccessInfoRepository  {
    private val logger = LoggerFactory.getLogger(javaClass)

    override val userInfoCacheMap: Lazy<ConcurrentHashMap<String, UserTokenInfo>>
        get() = lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            logger.error("##### RdbUserAccessInfo repository")
            val tmp = ConcurrentHashMap<String, UserTokenInfo>()
            val totUserInfoEntities = acctInfoRepository.findAll()
            for(entity in totUserInfoEntities) {
                val userTokenInfo = UserTokenInfo(
                    appKey = entity.appKey,
                    appSecret = entity.appSecret,
                    acctId = entity.acctId,
                    userCano = entity.cano,
                    userAcntPrdtNo = entity.userAcntPrdtNo
                )
                tmp[entity.acctId] = userTokenInfo
            }
            tmp
        }
}