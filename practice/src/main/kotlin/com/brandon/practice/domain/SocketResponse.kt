package com.brandon.practice.domain

import com.fasterxml.jackson.annotation.JsonProperty

data class SocketResponse(
    val header: Header,
    val body: Body?=null
) {
    class Header(
        @JsonProperty("tr_id")
        val trId: String?,
        val datetime: String?,
        val trKey: String? = null,
        val encrypt: String? = null
    )

    class Body(
        @JsonProperty("rt_cd")
        val rtCd: String? = null,
        @JsonProperty("msg_cd")
        val msgCd: String? = null,
        @JsonProperty("msg1")
        val msg1: String? = null
    )
}
