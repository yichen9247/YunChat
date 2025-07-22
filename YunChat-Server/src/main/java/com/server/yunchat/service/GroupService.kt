package com.server.yunchat.service

import com.server.yunchat.builder.model.GroupModel
import com.server.yunchat.builder.model.MemberModel

interface GroupService {
    fun getGroupInfo(gid: Int): GroupModel?
    fun setGroupStatus(gid: Int, status: Int, groupModel: GroupModel): GroupModel
    fun addGroupMember(gid: Int, uid: Long, memberModel: MemberModel): MemberModel
    fun addGroupInfo(name: String, avatar: String, notice: String, groupModel: GroupModel): GroupModel
    fun setGroupInfo(gid: Int, name: String, avatar: String, notice: String, groupModel: GroupModel): GroupModel
}