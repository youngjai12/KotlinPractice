package com.brandon.practice.repository

import java.util.concurrent.ConcurrentHashMap
import com.brandon.practice.repository.UserAccessInfoRepository.*


class RdbUserAccessInfoRepository(
    private val acctInfoRepository: AcctInfoRepository
): UserAccessInfoRepository  {
    override val userInfoCacheMap: Lazy<ConcurrentHashMap<String, UserTokenInfo>>
        get() = lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
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