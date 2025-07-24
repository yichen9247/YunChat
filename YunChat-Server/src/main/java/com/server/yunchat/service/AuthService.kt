package com.server.yunchat.service

import com.corundumstudio.socketio.AuthorizationResult
import com.corundumstudio.socketio.HandshakeData
import com.server.yunchat.client.mod.ClientUserModel

interface AuthService {
    fun validRegisterStatus()
    fun getClientAllowedOrigins(): List<String>
    fun validChatMessage(uid: Long, obj: Int, tar: Long)
    fun getGlobalTabooStatus(user: ClientUserModel): Boolean
    fun getGroupTabooStatus(user: ClientUserModel, gid: Int): Boolean
    fun validSocketAuthorization(data: HandshakeData): AuthorizationResult
}