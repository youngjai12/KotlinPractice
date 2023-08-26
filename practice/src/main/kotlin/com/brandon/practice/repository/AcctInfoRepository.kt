package com.brandon.practice.repository

import com.brandon.practice.domain.UserAcctInfoEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface AcctInfoRepository: JpaRepository<UserAcctInfoEntity, Long> {
    override fun findAll(): MutableList<UserAcctInfoEntity>

    @Query("SELECT u FROM UserAcctInfoEntity u WHERE u.acctId = :acctId")
    fun findByAcctId(acctId: String): MutableList<UserAcctInfoEntity>
}