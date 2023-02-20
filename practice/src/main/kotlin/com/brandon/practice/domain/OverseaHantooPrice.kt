package com.brandon.practice.domain

data class OverseaHantooPrice(
    val msg1: String?,
    val msg_cd: String?,
    val output: Output?,
    val rt_cd: String?
) {
    data class Output(
        val base: String?,
        val diff: String?,
        val last: String?,
        val ordy: String?,
        val pvol: String?,
        val rate: String?,
        val rsym: String?,
        val sign: String?,
        val tamt: String?,
        val tvol: String?,
        val zdiv: String?
    )
}
