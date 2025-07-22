package com.server.yunchat.service

import com.server.yunchat.client.mod.ClientUserModel

interface AuthService {
    fun validRegisterStatus()
    fun validChatMessage(uid: Long, obj: Int, tar: Long)
    fun getGlobalTabooStatus(user: ClientUserModel): Boolean
    fun getGroupTabooStatus(user: ClientUserModel, gid: Int): Boolean
}