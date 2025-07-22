package com.server.yunchat.builder.data

import com.fasterxml.jackson.annotation.JsonInclude

data class ResultModel(
    val code: Int,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val data: Any?, // 为null时，表示没有数据
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class CommonSearchPage(
    val page: Int? = null,
    val limit: Int? = null
)

data class CommonCheckVersion(
    val version: String? = null
)

data class RedisUserModel(
    val uid: Long = 0L,
    val token: String? = null,
    val platform: String? = "Unknown",
)