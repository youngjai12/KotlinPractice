package com.brandon.practice.repository

import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentHashMap

interface UserAccessInfoRepository {

    data class UserTokenInfo(
        val appKey: String,
        val appSecret: String,
        val acctId: String,
        var accessToken: String? = null,
        var accessTokenCreatedAt: Long? = null,
        val userCano: String,
        val userAcntPrdtNo: String
    )

    val userInfoCacheMap: Lazy<ConcurrentHashMap<String, UserTokenInfo>>

    fun getAvailableAcctIdList(): List<String> {
        return userInfoCacheMap.value.keys().toList()
    }

    fun getUserInfoByAcctId(acctId: String): UserTokenInfo? {
        return userInfoCacheMap.value[acctId]
    }
}