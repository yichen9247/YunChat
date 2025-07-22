package com.server.yunchat.builder.data

data class ControllerUserBind(
    val openid: String? = null,
    val access_token: String? = null
)

data class TencentValidModel(
    val error: Long? = null,
    val openid: String? = null,
    val client_id: String? = null
)

data class UserDecryptLoginModel(
    val username: String,
    val password: String,
    val timestamp: Long,
    val platform: String
)

data class UserAuthorizationModel(
    val uid: Long,
    val token: String,
    val timestamp: Long,
    val platform: String
)