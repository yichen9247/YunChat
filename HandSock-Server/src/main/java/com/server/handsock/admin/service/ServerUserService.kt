package com.server.handsock.admin.service

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.server.handsock.admin.dao.ServerUserDao
import com.server.handsock.admin.man.ServerUserManage
import com.server.handsock.admin.mod.ServerUserModel
import com.server.handsock.client.dao.ClientUserDao
import com.server.handsock.common.types.UserAuthType
import com.server.handsock.common.utils.HandUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class ServerUserService @Autowired constructor(
    private val serverUserDao: ServerUserDao,
    private val clientUserDao: ClientUserDao
) {
    fun getUserList(page: Int, limit: Int): Map<String, Any> {
        val pageObj = Page<ServerUserModel>(page.toLong(), limit.toLong())
        val result = serverUserDao.selectPage(pageObj, QueryWrapper<ServerUserModel>().orderByDesc("reg_time"))
        return mapOf("code" to 200, "data" to mapOf("total" to result.total, "items" to result.records), "message" to "获取成功")
    }

    fun deleteUser(uid: Long): Map<String, Any> {
        if (checkAdmin(uid)) return HandUtils.handleResultByCode(HttpStatus.NOT_ACCEPTABLE, null, "无法操作管理员账号")
        return if (serverUserDao.deleteById(uid) > 0) {
            HandUtils.handleResultByCode(HttpStatus.OK, null, "删除成功")
        } else HandUtils.handleResultByCode(HttpStatus.INTERNAL_SERVER_ERROR, null, "删除失败")
    }

    fun updateUserPassword(uid: Long, password: String): Map<String, Any> {
        if (HandUtils.isValidPassword(password)) return HandUtils.handleResultByCode(HttpStatus.NOT_ACCEPTABLE, null, "密码格式不合规")
        if (checkAdmin(uid)) return HandUtils.handleResultByCode(HttpStatus.NOT_ACCEPTABLE, null, "无法操作管理员账号")
        return serverUserDao.selectById(uid)?.let { user ->
            ServerUserManage(HandUtils).updateUserPassword(user, uid, password)
            if (serverUserDao.updateById(user) > 0) HandUtils.handleResultByCode(HttpStatus.OK, null, "修改密码成功")
            else HandUtils.handleResultByCode(HttpStatus.INTERNAL_SERVER_ERROR, null, "修改密码失败")
        } ?: HandUtils.handleResultByCode(HttpStatus.NOT_ACCEPTABLE, null, "用户不存在")
    }

    fun updateUserStatus(uid: Long, status: Int): Map<String, Any> {
        if (checkAdmin(uid)) return HandUtils.handleResultByCode(HttpStatus.NOT_ACCEPTABLE, null, "无法操作管理员账号")
        return serverUserDao.selectById(uid)?.let { user ->
            user.status = status
            if (serverUserDao.updateById(user) > 0) HandUtils.handleResultByCode(HttpStatus.OK, null, "状态更新成功")
            else HandUtils.handleResultByCode(HttpStatus.INTERNAL_SERVER_ERROR, null, "状态更新失败")
        } ?: HandUtils.handleResultByCode(HttpStatus.NOT_ACCEPTABLE, null, "用户不存在")
    }

    fun updateUserInfo(uid: Long, username: String, nick: String, avatar: String, robot: Boolean): Map<String, Any> {
        if (nick.length > 10 || nick.length < 2 || HandUtils.isValidUsername(username)) return HandUtils.handleResultByCode(HttpStatus.NOT_ACCEPTABLE, null, "昵称或账号有误")
        if (checkAdmin(uid)) return HandUtils.handleResultByCode(HttpStatus.NOT_ACCEPTABLE, null, "无法操作管理员账号")
        return serverUserDao.selectById(uid)?.let { user ->
            if (robot) {
                serverUserDao.selectList(QueryWrapper<ServerUserModel>()
                    .eq("permission", UserAuthType.ROBOT_AUTHENTICATION)
                    .ne("uid", uid))
                    .filter { it.permission != UserAuthType.ADMIN_AUTHENTICATION }
                    .forEach {
                        it.permission = UserAuthType.USER_AUTHENTICATION
                        serverUserDao.updateById(it)
                    }
                ServerUserManage(HandUtils).updateUserInfo(user, uid, username, nick, avatar, UserAuthType.ROBOT_AUTHENTICATION)
            } else ServerUserManage(HandUtils).updateUserInfo(user, uid, username, nick, avatar, UserAuthType.USER_AUTHENTICATION)
            if (serverUserDao.updateById(user) > 0) HandUtils.handleResultByCode(HttpStatus.OK, null, "信息更新成功")
            else HandUtils.handleResultByCode(HttpStatus.INTERNAL_SERVER_ERROR, null, "信息更新失败")
        } ?: HandUtils.handleResultByCode(HttpStatus.NOT_ACCEPTABLE, null, "用户不存在")
    }

    private fun checkAdmin(uid: Long): Boolean {
        return clientUserDao.selectById(uid)?.let {
            it.permission == UserAuthType.ADMIN_AUTHENTICATION
        } ?: false
    }
}