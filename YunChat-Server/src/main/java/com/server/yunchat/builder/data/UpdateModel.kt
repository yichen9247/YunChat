package com.server.yunchat.builder.data

data class UserUpdateNick(
    val nick: String?= null
)

data class UserUpdateAvatar(
    val path: String?= null
)

data class UserUpdatePassword(
    val password: String?= null
)