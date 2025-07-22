package com.server.yunchat.service

import com.server.yunchat.admin.mod.ServerUserModel
import com.server.yunchat.builder.model.MemberModel
import com.server.yunchat.client.mod.ClientUserModel

interface UserService {
    fun getRegisterUid(): Long
    fun getRandomAvatar(): String
    fun getUserAiAuth(uid: Long): Boolean
    fun getUserIsRobot(uid: Long): Boolean
    fun getUserIsAdmin(uid: Long): Boolean
    fun setUserAiAuth(uid: Long, status: Boolean)
    fun getClientUserInfo(uid: Long): ClientUserModel?
    fun setUserQqId(uid: Long, qqId: String?, serverUserModel: ServerUserModel)
    fun setUserPassword(uid: Long, password: String, serverUserModel: ServerUserModel)
    fun setUserStatus(uid: Long, status: Int, serverUserModel: ServerUserModel): ServerUserModel
    fun setUserNick(uid: Long, nick: String, serverUserModel: ServerUserModel): Map<String, Any>
    fun setUserAvatar(uid: Long, path: String, serverUserModel: ServerUserModel): Map<String, Any>
    fun setUserInfo(uid: Long, nick: String, robot: Boolean, avatar: String, serverUserModel: ServerUserModel) : ServerUserModel
    fun setRegisterUser(username: String, password: String, memberModel: MemberModel, serverUserModel: ServerUserModel): ServerUserModel
}