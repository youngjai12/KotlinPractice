package com.brandon.practice.domain

import javax.persistence.*

@Entity
@Table(name = "user_acct_info_hantoo")
data class UserAcctInfoEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    val id: Long? = null,

    @Column(name="acct_id")
    val acctId: String,

    @Column(name="user_id")
    val userId: String,

    @Column(name="appkey")
    val appKey: String,

    @Column(name = "appsecret")
    val appSecret: String,

    @Column(name = "cano")
    val cano: String,

    @Column(name = "user_acnt_prdt_no")
    val userAcntPrdtNo: String
)
