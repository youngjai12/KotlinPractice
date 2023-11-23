package com.brandon.practice.controller

import com.brandon.practice.repository.RdbUserAccessInfoRepository
import com.brandon.practice.repository.UserAccessInfoRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class UserController(
    val memberRepository: UserAccessInfoRepository
) {

    @GetMapping("/user/delete/{acctId}")
    fun getAvailableUsers(@PathVariable(value = "acctId") acctId: String): List<String> {
        return memberRepository.getAvailableAcctIdList()
    }
}