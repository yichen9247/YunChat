package com.server.yunchat.service

interface TokenService {
    fun verifyUserToken(token: String): Boolean
    fun setUserToken(uid: Long, username: String): String
    fun createUserToken(uid: Long, username: String): String
    fun getUserTokenStatus(uid: Long, token: String): Boolean
}