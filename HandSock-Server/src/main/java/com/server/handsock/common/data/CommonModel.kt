package com.server.handsock.common.data

import java.time.LocalDateTime

data class ResultModel(
    val code: Int,
    val data: Any?,
    val message: String,
    val datetime: LocalDateTime = LocalDateTime.now()
)

data class CommonSearchPage(
    val pid: Int? = null,
    val page: Int? = null,
    val limit: Int? = null
)

data class CommonCheckVersion(
    val version: String? = null
)