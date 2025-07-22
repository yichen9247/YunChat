package com.server.yunchat.service

import com.server.yunchat.builder.data.UserAuthorizationModel
import com.server.yunchat.builder.data.UserDecryptLoginModel

interface EncryptService {
    fun decryptLogin(data: String): UserDecryptLoginModel?
    fun decryptAuthorization(authHeader: String, validTimestamp: Boolean): UserAuthorizationModel?
}