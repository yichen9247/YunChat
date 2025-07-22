package com.server.yunchat.admin.service

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.server.yunchat.admin.dao.ServerUserDao
import com.server.yunchat.admin.mod.ServerUserModel
import com.server.yunchat.builder.data.ResultModel
import com.server.yunchat.builder.utils.HandUtils
import com.server.yunchat.plugin.TaskPlugin
import com.server.yunchat.service.impl.UserServiceImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class ServerUserService @Autowired constructor(
    private val taskPlugin: TaskPlugin,
    private val serverUserDao: ServerUserDao,
    private val userServiceImpl: UserServiceImpl
) {
    fun getUserList(page: Int, limit: Int): ResultModel {
        val pageObj = Page<ServerUserModel>(page.toLong(), limit.toLong())
        val result = serverUserDao.selectPage(pageObj, QueryWrapper<ServerUserModel>().orderByDesc("reg_time"))
        return HandUtils.handleResultByCode(HttpStatus.OK, mapOf(
            "total" to result.total,
            "items" to result.records
        ), "获取成功")
    }

    fun deleteUser(uid: Long): ResultModel {
        if (userServiceImpl.getUserIsAdmin(uid))
            return HandUtils.handleResultByCode(
                HttpStatus.NOT_ACCEPTABLE, null, "无法删除管理员账号"
            )
        if (userServiceImpl.getUserIsRobot(uid))
            return HandUtils.handleResultByCode(
                HttpStatus.NOT_ACCEPTABLE, null, "无法删除机器人账号"
            )
        return if (serverUserDao.deleteById(uid) > 0) {
            taskPlugin.doDeleteRedisCache() // 清除缓存
            HandUtils.handleResultByCode(HttpStatus.OK, null, "删除用户成功")
        } else HandUtils.handleResultByCode(HttpStatus.INTERNAL_SERVER_ERROR, null, "删除用户失败")
    }

    fun updateUserPassword(uid: Long, password: String): ResultModel {
        val serverUserModel = ServerUserModel()
        return try {
            val result = userServiceImpl.setUserPassword(uid, password, serverUserModel)
            if (serverUserDao.updateById(serverUserModel) > 0) {
                HandUtils.handleResultByCode(HttpStatus.OK, result, "修改密码成功")
            } else HandUtils.handleResultByCode(HttpStatus.INTERNAL_SERVER_ERROR, result, "修改密码失败")
        } catch (e: Exception) {
            HandUtils.handleResultByCode(HttpStatus.BAD_REQUEST, null, e.message.toString())
        }
    }

    fun updateUserStatus(uid: Long, status: Int): ResultModel {
        val serverUserModel = ServerUserModel()
        return try {
            val result = userServiceImpl.setUserStatus(uid, status, serverUserModel)
            if (serverUserDao.updateById(serverUserModel) > 0) {
                taskPlugin.doDeleteRedisCache() // 清除缓存
                HandUtils.handleResultByCode(HttpStatus.OK, result, "修改状态成功")
            } else HandUtils.handleResultByCode(HttpStatus.INTERNAL_SERVER_ERROR, result, "修改状态失败")
        } catch (e: Exception) {
            HandUtils.handleResultByCode(HttpStatus.BAD_REQUEST, null, e.message.toString())
        }
    }

    fun updateUserInfo(uid: Long, nick: String, avatar: String, robot: Boolean): ResultModel {
        val serverUserModel = ServerUserModel()
        return try {
            val result = userServiceImpl.setUserInfo(uid, nick, robot, avatar, serverUserModel)
            if (serverUserDao.updateById(serverUserModel) > 0) {
                taskPlugin.doDeleteRedisCache() // 清除缓存
                HandUtils.handleResultByCode(HttpStatus.OK, result, "修改信息成功")
            } else HandUtils.handleResultByCode(HttpStatus.INTERNAL_SERVER_ERROR, result, "修改信息失败")
        } catch (e: Exception) {
            HandUtils.handleResultByCode(HttpStatus.BAD_REQUEST, null, e.message.toString())
        }
    }
}