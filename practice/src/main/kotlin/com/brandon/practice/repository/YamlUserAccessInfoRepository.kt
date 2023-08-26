package com.brandon.practice.repository

import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import java.util.concurrent.ConcurrentHashMap
import com.brandon.practice.repository.UserAccessInfoRepository.*
import org.slf4j.LoggerFactory

@Configuration
@ConfigurationProperties(prefix = "auth")
class YamlUserAccessInfoRepository: UserAccessInfoRepository {

    private val appkey = ConcurrentHashMap<String, String>()
    private val appsecret = ConcurrentHashMap<String, String>()
    private val accesstoken= ConcurrentHashMap<String, String>()
    @JsonProperty("user_cano")
    private val userCano = ConcurrentHashMap<String, String>()

    private val logger = LoggerFactory.getLogger(javaClass)

    override val userInfoCacheMap: Lazy<ConcurrentHashMap<String, UserTokenInfo>>
        get() = lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            logger.info("[Yaml] :: userInfoCacheMap Update !!")
            val tmp = ConcurrentHashMap<String, UserTokenInfo>()
            for(element in appkey) {
                val userTokenInfo = UserTokenInfo(
                    appKey = element.value,
                    appSecret = appsecret[element.key]!!,
                    acctId = element.key,
                    accessToken = accesstoken[element.key],
                    userCano = userCano[element.key]!!,
                    userAcntPrdtNo = "01"
                )
                tmp.put(element.key, userTokenInfo)
            }
            tmp
        }

    override fun getAvailableAcctIdList(): List<String> {
        return userInfoCacheMap.value.keys.toList()
    }

    override fun getExistingUserIdList(): List<String> {
        return userInfoCacheMap.value.keys.toList()
    }

    override fun getUserInfoByAcctId(acctId: String): UserTokenInfo? {
        return userInfoCacheMap.value[acctId]
    }
}
