package com.brandon.practice.module

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties("user")
class UserInfoProperties (
    // yml파일에서 list 형태로 씌여진게 아니고, key-value형태임.
    val appkey: Map<String, String>,

    //
    val appsecret: List<Map<String, String>>,
    val accesstoken: List<Map<String, String>>
) {
    fun getAppSecret(acctId: String): String? {
       for (item in appsecret) {
           if(item[acctId] !=null){
               return item[acctId]
           }
       }
        return null
    }
}
