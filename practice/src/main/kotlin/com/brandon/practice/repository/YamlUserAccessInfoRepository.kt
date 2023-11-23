package com.brandon.practice.repository

import java.util.concurrent.ConcurrentHashMap
import com.brandon.practice.repository.UserAccessInfoRepository.*
import org.slf4j.LoggerFactory


class YamlUserAccessInfoRepository(
    private val yamlAuthParser: AuthYamlParser
): UserAccessInfoRepository {
    private val logger = LoggerFactory.getLogger(javaClass)

    override val userInfoCacheMap: Lazy<ConcurrentHashMap<String, UserTokenInfo>>
        get() = lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            logger.info("[Yaml] :: userInfoCacheMap Update !!")
            val tmp = ConcurrentHashMap<String, UserTokenInfo>()
            val appkey = yamlAuthParser.getAppKeys()
            val accessTokens = yamlAuthParser.getAccessTokens()
            val appSecrets = yamlAuthParser.getAppSecrets()
            val userCanos = yamlAuthParser.userCano
            for(acctId in appkey.keys) {
                logger.info("## info : ${appkey[acctId]} ${appSecrets[acctId]} ${accessTokens[acctId]}")
                val userTokenInfo = UserTokenInfo(
                    appKey = appkey[acctId]!!,
                    appSecret = appSecrets[acctId]!!,
                    acctId = acctId,
                    accessToken = accessTokens[acctId],
                    userCano = userCanos?.get(acctId)!!,
                    userAcntPrdtNo = "01"
                )
                tmp[acctId] = userTokenInfo
            }
            logger.info("tmp : ${tmp}")
            tmp
        }

    override fun getAvailableAcctIdList(): List<String> {
        return userInfoCacheMap.value.keys.toList()
    }

    override fun getUserInfoByAcctId(acctId: String): UserTokenInfo? {
        return userInfoCacheMap.value[acctId]
    }
}
