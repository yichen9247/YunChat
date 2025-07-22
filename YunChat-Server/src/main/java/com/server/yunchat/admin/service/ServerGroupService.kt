package com.server.yunchat.admin.service

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.server.yunchat.builder.dao.GroupDao
import com.server.yunchat.builder.dao.MemberDao
import com.server.yunchat.builder.data.ResultModel
import com.server.yunchat.builder.model.GroupModel
import com.server.yunchat.builder.model.MemberModel
import com.server.yunchat.builder.utils.HandUtils
import com.server.yunchat.client.service.ClientGroupService
import com.server.yunchat.plugin.TaskPlugin
import com.server.yunchat.service.impl.GroupServiceImpl
import com.server.yunchat.service.impl.UserServiceImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class ServerGroupService @Autowired constructor(
    private val groupDao: GroupDao,
    private val memberDao: MemberDao,
    private val taskPlugin: TaskPlugin,
    private val userServiceImpl: UserServiceImpl,
    private val groupServiceImpl: GroupServiceImpl,
    private val clientGroupService: ClientGroupService
) {
    fun getGroupList(page: Int, limit: Int): ResultModel {
        val queryResult = groupDao.getAllGroupList(page - 1, limit)
        return HandUtils.handleResultByCode(HttpStatus.OK,  mapOf(
            "items" to queryResult,
            "total" to queryResult.size
        ), "获取成功")
    }

    fun deleteGroup(gid: Int): ResultModel {
        if (gid == 1)
            return HandUtils.handleResultByCode(
                HttpStatus.NOT_ACCEPTABLE, null, "默认群聊不可被删除"
            )
        return try {
            if (groupDao.deleteById(gid) > 0) {
                HandUtils.handleResultByCode(HttpStatus.OK, null, "删除群聊成功")
            } else HandUtils.handleResultByCode(HttpStatus.NOT_ACCEPTABLE, null, "删除群聊失败")
        } catch (e: Exception) {
            HandUtils.handleResultByCode(HttpStatus.INTERNAL_SERVER_ERROR, null, e.message.toString())
        }
    }

    fun updateGroup(gid: Int, name: String, avatar: String, notice: String): ResultModel {
        return try {
            val groupModel = GroupModel()
            val result = groupServiceImpl.setGroupInfo(gid, name, avatar, notice, groupModel)
            if (groupDao.updateById(result) > 0) {
                taskPlugin.doDeleteRedisCache() // 清除缓存
                HandUtils.handleResultByCode(HttpStatus.OK, null, "修改信息成功")
            } else HandUtils.handleResultByCode(HttpStatus.INTERNAL_SERVER_ERROR, null, "修改信息失败")
        } catch (e: Exception) {
            HandUtils.handleResultByCode(HttpStatus.INTERNAL_SERVER_ERROR, null, e.message.toString())
        }
    }

    fun createGroup(name: String, avatar: String, notice: String, uid: Long): ResultModel {
        return try {
            val groupModel = GroupModel()
            val result = groupServiceImpl.addGroupInfo(name, avatar, notice, groupModel)
            if (groupDao.insert(result) > 0) {
                taskPlugin.doDeleteRedisCache() // 清除缓存
                clientGroupService.addGroupMember(result.gid, uid)
                HandUtils.handleResultByCode(HttpStatus.OK, null, "创建群聊成功")
            } else HandUtils.handleResultByCode(HttpStatus.INTERNAL_SERVER_ERROR, null, "创建群聊失败")
        } catch (e: Exception) {
            HandUtils.handleResultByCode(HttpStatus.INTERNAL_SERVER_ERROR, null, e.message.toString())
        }
    }

    fun updateGroupStatus(gid: Int, status: Int): ResultModel {
        return try {
            val groupModel = GroupModel()
            val result = groupServiceImpl.setGroupStatus(gid, status, groupModel)
            if (groupDao.updateById(result) > 0) {
                taskPlugin.doDeleteRedisCache() // 清除缓存
                HandUtils.handleResultByCode(HttpStatus.OK, null, "修改状态成功")
            } else HandUtils.handleResultByCode(HttpStatus.INTERNAL_SERVER_ERROR, null, "修改状态失败")
        } catch (e: Exception) {
            HandUtils.handleResultByCode(HttpStatus.INTERNAL_SERVER_ERROR, null, e.message.toString())
        }
    }

    fun deleteGroupMember(gid: Int, uid: Long): ResultModel {
        if (gid == 1)
            return HandUtils.handleResultByCode(
                HttpStatus.NOT_ACCEPTABLE, null, "默认群聊不可被移出"
            )
        if (userServiceImpl.getUserIsAdmin(uid))
            return HandUtils.handleResultByCode(
                HttpStatus.NOT_ACCEPTABLE, null, "无法操作管理员账号"
            )
        return try {
            val deleteWrapper = QueryWrapper<MemberModel>().eq("gid", gid).eq("uid", uid)
            return if (memberDao.delete(deleteWrapper) > 0) {
                taskPlugin.doDeleteRedisCache() // 清除缓存
                HandUtils.handleResultByCode(HttpStatus.OK, null, "移出成员成功")
            } else HandUtils.handleResultByCode(HttpStatus.INTERNAL_SERVER_ERROR, null, "移出成员失败")
        } catch (e: Exception) {
            HandUtils.handleResultByCode(HttpStatus.INTERNAL_SERVER_ERROR, null, e.message.toString())
        }
    }
}
