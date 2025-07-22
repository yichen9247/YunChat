package com.server.yunchat.client.service

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper
import com.server.yunchat.admin.dao.ServerUserDao
import com.server.yunchat.admin.mod.ServerUserModel
import com.server.yunchat.builder.dao.MemberDao
import com.server.yunchat.builder.data.ControllerUserBind
import com.server.yunchat.builder.data.ResultModel
import com.server.yunchat.builder.model.MemberModel
import com.server.yunchat.builder.service.AsyncService
import com.server.yunchat.builder.utils.ConsoleUtils
import com.server.yunchat.builder.utils.HandUtils
import com.server.yunchat.client.dao.ClientUserDao
import com.server.yunchat.plugin.SpringPlugin
import com.server.yunchat.plugin.TaskPlugin
import com.server.yunchat.service.impl.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
open class ClientUserService @Autowired constructor(
    private val memberDao: MemberDao,
    private val taskPlugin: TaskPlugin,
    private val asyncService: AsyncService,
    private val springPlugin: SpringPlugin,
    private val serverUserDao: ServerUserDao,
    private val clientUserDao: ClientUserDao,
    private val authServiceImpl: AuthServiceImpl,
    private val userServiceImpl: UserServiceImpl,
    private val redisServiceImpl: RedisServiceImpl,
    private val tokenServiceImpl: TokenServiceImpl,
    private val socketServiceImpl: SocketServiceImpl,
    private val onlineServiceImpl: OnlineServiceImpl,
    private val requestServiceImpl: RequestServiceImpl
) {
    fun queryAllUser(): ResultModel {
        return HandUtils.handleResultByCode(HttpStatus.OK, clientUserDao.selectList(null), "获取成功")
    }

    fun getUserInfo(uid: Long): ResultModel {
        val selectResult = userServiceImpl.getClientUserInfo(uid)
        return if (selectResult != null) {
            HandUtils.handleResultByCode(HttpStatus.OK, selectResult, "获取成功")
        } else HandUtils.handleResultByCode(HttpStatus.NOT_ACCEPTABLE, null, "用户不存在")
    }

    fun getUserBind(uid: Long): ResultModel {
        val selectResult = serverUserDao.selectOne(QueryWrapper<ServerUserModel>().eq("uid", uid))
        return if (selectResult == null) {
            HandUtils.handleResultByCode(HttpStatus.NOT_ACCEPTABLE, null, "未查询到用户")
        } else {
            HandUtils.handleResultByCode(HttpStatus.OK, mapOf(
                "qq" to (selectResult.qqId != null)
            ), "获取成功")
        }
    }

    fun bindUserTencentId(uid: Long, data: ControllerUserBind): ResultModel {
        return if (requestServiceImpl.validTencentLogin(data)) {
            try {
                val updateWrapper: UpdateWrapper<ServerUserModel> = UpdateWrapper<ServerUserModel>()
                updateWrapper
                    .eq("uid", uid)
                    .set("qq_id", data.openid)
                if (serverUserDao.update(updateWrapper) > 0) {
                    HandUtils.handleResultByCode(HttpStatus.OK, null, "绑定成功")
                } else HandUtils.handleResultByCode(HttpStatus.INTERNAL_SERVER_ERROR, null, "绑定失败")
            } catch (e: DataIntegrityViolationException) {
                HandUtils.handleResultByCode(HttpStatus.NOT_ACCEPTABLE, null, "该QQ号已被其它账号绑定")
            }
        } else HandUtils.handleResultByCode(HttpStatus.FORBIDDEN, null, "登录状态异常请重试")
    }

    fun unBindUserTencentId(uid: Long): ResultModel {
        return try {
            val updateWrapper: UpdateWrapper<ServerUserModel> = UpdateWrapper<ServerUserModel>()
            updateWrapper
                .set("qq_id", null)
                .eq("uid", uid)
            if (serverUserDao.update(updateWrapper) > 0) {
                HandUtils.handleResultByCode(HttpStatus.OK, null, "解除绑定成功")
            } else HandUtils.handleResultByCode(HttpStatus.INTERNAL_SERVER_ERROR, null, "解除绑定失败")
        } catch (e: Exception) {
            HandUtils.handleResultByCode(HttpStatus.INTERNAL_SERVER_ERROR, null, "解除绑定失败")
        }
    }

    fun loginUser(username: String, password: String, address: String, platform: String): ResultModel {
        val selectResult = serverUserDao.selectOne(QueryWrapper<ServerUserModel>().eq("username", username))
        return try {
            springPlugin.validField(ServerUserModel().let {
                it.username = username
                it.password = password
                springPlugin.validField(it)
            }) // 验证登录信息字段
            if (selectResult == null || selectResult.password != HandUtils.encodeStringToMD5(password)) {
                HandUtils.handleResultByCode(HttpStatus.NOT_ACCEPTABLE, null, "账号或密码错误")
            } else userLogin(selectResult, username, address, platform)
        } catch (e: Exception) {
            HandUtils.handleResultByCode(HttpStatus.BAD_REQUEST, null, e.message.toString())
        }
    }

    fun registerUser(username: String, password: String, address: String, platform: String): ResultModel {
        val memberModel = MemberModel()
        val serverUserModel = ServerUserModel()
        return try {
            authServiceImpl.validRegisterStatus()
            userServiceImpl.setRegisterUser(username, password, memberModel, serverUserModel)
            if (serverUserDao.insert(serverUserModel) > 0 && memberDao.insert(memberModel) > 0) {
                val token = tokenServiceImpl.setUserToken(serverUserModel.uid, username)
                ConsoleUtils.printCustomLogs(
                    message = token,
                    address = address,
                    uid = serverUserModel.uid,
                    content = "User Register",
                ) // 打印注册日志
                asyncService.insertUserLoginLog(
                    address = address,
                    platform = platform,
                    uid = serverUserModel.uid,
                    username = serverUserModel.username,
                ) // 写入登录日志
                onlineServiceImpl.sendNewUserWelcome(
                    nick = serverUserModel.nick,
                    username = serverUserModel.username
                ) // 发送新人欢迎
                HandUtils.handleResultByCode(HttpStatus.OK, mapOf(
                    "token" to token, "userinfo" to serverUserModel
                ), "注册成功")
            } else HandUtils.handleResultByCode(HttpStatus.INTERNAL_SERVER_ERROR, null, "注册失败")
        } catch (e: DataIntegrityViolationException) {
            HandUtils.handleResultByCode(HttpStatus.CONFLICT, null, "用户名已存在")
        } catch (e: Exception) {
            HandUtils.handleResultByCode(HttpStatus.INTERNAL_SERVER_ERROR, null, e.message.toString())
        }
    }

    fun loginUserWithQQ(data: ControllerUserBind, address: String): ResultModel {
        return if (requestServiceImpl.validTencentLogin(data)) {
            val selectResult = serverUserDao.selectOne(QueryWrapper<ServerUserModel>().eq("qq_id", data.openid))
            if (selectResult == null) {
                HandUtils.handleResultByCode(HttpStatus.NOT_ACCEPTABLE, null, "请先注册账号")
            } else userLogin(selectResult, selectResult.username, address, "APP")
        } else HandUtils.handleResultByCode(HttpStatus.FORBIDDEN, null, "登录状态异常请重试")
    }

    fun updateUserNick(uid: Long, nick: String): ResultModel {
        val serverUserModel = ServerUserModel()
        return try {
            val result = userServiceImpl.setUserNick(uid, nick, serverUserModel)
            if (serverUserDao.updateById(serverUserModel) > 0) {
                taskPlugin.doDeleteRedisCache() // 清除缓存
                socketServiceImpl.sendGlobalMessage(
                    event = "[RE:USER:NICK]",
                    content = HandUtils.handleResultByCode(HttpStatus.OK, result, "用户昵称更新")
                )
                HandUtils.handleResultByCode(HttpStatus.OK, result, "修改昵称成功")
            } else HandUtils.handleResultByCode(HttpStatus.INTERNAL_SERVER_ERROR, result, "修改昵称失败")
        } catch (e: Exception) {
            HandUtils.handleResultByCode(HttpStatus.BAD_REQUEST, null, e.message.toString())
        }
    }

    fun updateUserAvatar(uid: Long, path: String): ResultModel {
        val serverUserModel = ServerUserModel()
        return try {
            val result = userServiceImpl.setUserAvatar(uid, path, serverUserModel)
            if (serverUserDao.updateById(serverUserModel) > 0) {
                taskPlugin.doDeleteRedisCache() // 清除缓存
                socketServiceImpl.sendGlobalMessage(
                    event = "[RE:USER:AVATAR]",
                    content = HandUtils.handleResultByCode(HttpStatus.OK, result, "修改头像成功")
                )
                HandUtils.handleResultByCode(HttpStatus.OK, result, "修改头像成功")
            } else HandUtils.handleResultByCode(HttpStatus.INTERNAL_SERVER_ERROR, result, "修改头像失败")
        } catch (e: Exception) {
            HandUtils.handleResultByCode(HttpStatus.BAD_REQUEST, null, e.message.toString())
        }
    }

    fun updateUserPassword(uid: Long, password: String): ResultModel {
        val serverUserModel = ServerUserModel()
        return try {
            userServiceImpl.setUserPassword(uid, password, serverUserModel)
            if (serverUserDao.updateById(serverUserModel) > 0) {
                HandUtils.handleResultByCode(HttpStatus.OK, null, "修改密码成功")
            } else HandUtils.handleResultByCode(HttpStatus.INTERNAL_SERVER_ERROR, null, "修改密码失败")
        } catch (e: Exception) {
            HandUtils.handleResultByCode(HttpStatus.BAD_REQUEST, null, e.message.toString())
        }
    }

    fun getUserQrcodeScanStatus(qid: String, address: String): ResultModel {
        val status = redisServiceImpl.getUserScanStatus(qid)
            ?: return HandUtils.handleResultByCode(HttpStatus.UNAUTHORIZED, null, "二维码已过期")
        return when(status) {
            "0" -> HandUtils.handleResultByCode(HttpStatus.BAD_REQUEST, null, "等待扫码中")
            "1" -> {
                redisServiceImpl.delUserScanStatus(qid)
                val targetUser = redisServiceImpl.getUserScanResult(qid)
                    ?: return HandUtils.handleResultByCode(HttpStatus.INTERNAL_SERVER_ERROR, null, "服务器异常")
                val loginUser = clientUserDao.selectById(targetUser.toLong())
                loginUserScan(
                    address = address,
                    username = loginUser.username
                )
            }
            else -> HandUtils.handleResultByCode(HttpStatus.INTERNAL_SERVER_ERROR, null, "服务器异常")
        }
    }

    private fun userLogin(selectResult: ServerUserModel, username: String, address: String, platform: String): ResultModel {
        val uid = selectResult.uid
        val token = tokenServiceImpl.setUserToken(uid, username)
        asyncService.insertUserLoginLog(uid, username, address, platform)
        return HandUtils.handleResultByCode(HttpStatus.OK, mapOf(
            "token" to token,
            "userinfo" to selectResult
        ), "登录成功")
    }

    private fun loginUserScan(username: String, address: String): ResultModel {
        val selectResult = serverUserDao.selectOne(QueryWrapper<ServerUserModel>().eq("username", username))
        return if (selectResult == null) {
            HandUtils.handleResultByCode(HttpStatus.NOT_ACCEPTABLE, null, "未查询到用户")
        } else userLogin(selectResult, username, address, "Web")
    }
}
