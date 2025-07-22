package com.android.yunchat.service

interface EncryptService {
    fun encryptLogin(
        username: String,
        password: String
    ): String
    fun encryptAuthorization(): String
}