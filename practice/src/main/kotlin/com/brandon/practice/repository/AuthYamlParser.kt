package com.brandon.practice.repository

import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import java.util.concurrent.ConcurrentHashMap

@Configuration
@ConfigurationProperties(prefix = "user")
class AuthYamlParser {
//    private val appkey = ConcurrentHashMap<String, String>()
//    private val appsecret = ConcurrentHashMap<String, String>()
//    private val accesstoken= ConcurrentHashMap<String, String>()
//    @JsonProperty("user_cano")
//    private val userCano = ConcurrentHashMap<String, String>()


    var appkey: Map<String, String>? = null
        set(value) { field = value }


    var appsecret: Map<String, String>? = null
        set(value) { field = value }

    var accesstoken: Map<String, String>? = null
        set(value) { field = value }

    @JsonProperty("user_cano")
    var userCano: Map<String, String>? = null
        set(value) { field = value }


    fun getAcctIds(): List<String> {
        return appkey?.keys?.toList() ?: listOf()
    }

    fun getAppKeys(): Map<String, String> {
        return appkey ?: mapOf()
    }

    fun getAppSecrets() : Map<String, String> {
        return appsecret ?: mapOf()
    }

    fun getAccessTokens():  Map<String, String> {
        return accesstoken ?: mapOf()
    }

}