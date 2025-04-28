package com.server.handsock.client.service

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.server.handsock.admin.dao.ServerUserDao
import com.server.handsock.admin.mod.ServerUserModel
import com.server.handsock.client.dao.ClientUserDao
import com.server.handsock.client.man.ClientUserManage
import com.server.handsock.client.mod.ClientUserModel
import com.server.handsock.common.utils.ConsoleUtils
import com.server.handsock.common.utils.GlobalService.socketIOServer
import com.server.handsock.common.utils.HandUtils
import com.server.handsock.common.utils.IDGenerator
import com.server.handsock.service.TokenService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
open class ClientUserService @Autowired constructor(
    private val tokenService: TokenService,
    private val serverUserDao: ServerUserDao,
    private val clientUserDao: ClientUserDao,
) {
    fun queryUserInfo(uid: Long): ClientUserModel {
        return clientUserDao.selectById(uid)
    }

    fun queryAllUser(): Map<String, Any> {
        return HandUtils.handleResultByCode(HttpStatus.OK, clientUserDao.selectList(null), "获取成功")
    }

    fun robotInnerStatus(): ClientUserModel {
        val selectResult = clientUserDao.selectOne(QueryWrapper<ClientUserModel>().eq("permission", 2))
        return selectResult
    }

    fun getUserInfo(uid: Long): Map<String, Any> {
        val selectResult = clientUserDao.selectOne(QueryWrapper<ClientUserModel>().eq("uid", uid))
        return if (selectResult != null) {
            HandUtils.handleResultByCode(HttpStatus.OK, selectResult, "获取成功")
        } else HandUtils.handleResultByCode(HttpStatus.NOT_ACCEPTABLE, null, "用户不存在")
    }

    fun getUserBind(uid: Long): Map<String, Any> {
        val selectResult = serverUserDao.selectOne(QueryWrapper<ServerUserModel>().eq("uid", uid))
        return if (selectResult == null) {
            HandUtils.handleResultByCode(HttpStatus.NOT_ACCEPTABLE, null, "未查询到用户")
        } else {
            HandUtils.handleResultByCode(HttpStatus.OK, mapOf(
                "qq" to (selectResult.qqId != null),
                "wechat" to false
            ), "获取成功")
        }
    }

    fun bindUserWithQQ(uid: Long, qqId: String): Map<String, Any> {
        val bindSelect = serverUserDao.selectOne(QueryWrapper<ServerUserModel>().eq("qq_id", qqId))
        if (bindSelect != null) return HandUtils.handleResultByCode(HttpStatus.NOT_ACCEPTABLE, null, "该QQ号已被其它账号绑定")
        val serverUserModel = ServerUserModel()
        val result = ClientUserManage().updateQQId(serverUserModel, uid, qqId)
        return if (serverUserDao.updateById(serverUserModel) > 0) {
            HandUtils.handleResultByCode(HttpStatus.OK, result, "绑定成功")
        } else HandUtils.handleResultByCode(HttpStatus.INTERNAL_SERVER_ERROR, result, "绑定失败")
    }

    fun setUserAiAuthorization(uid: Long?, status: Boolean): Boolean {
        val clientUserModel = ClientUserModel()
        try {
            clientUserModel.uid = uid
            clientUserModel.aiAuth = if (status) 1 else 0
            return clientUserDao.updateById(clientUserModel) > 0
        } catch (e: Exception) {
            HandUtils.printErrorLog(e)
            return false
        }
    }

    fun loginUser(username: String, password: String, address: String): Map<String, Any> {
        val selectResult = serverUserDao.selectOne(QueryWrapper<ServerUserModel>().eq("username", username))
        return if (selectResult == null || selectResult.password != HandUtils.encodeStringToMD5(password)) {
            HandUtils.handleResultByCode(HttpStatus.NOT_ACCEPTABLE, null, "账号或密码错误")
        } else userLogin(
            address = address,
            username = username,
            selectResult = selectResult
        )
    }

    fun registerUser(username: String, password: String, address: String): Map<String, Any> {
        if (HandUtils.isValidUsername(username) || HandUtils.isValidPassword(password)) return HandUtils.handleResultByCode(HttpStatus.NOT_ACCEPTABLE, null, "输入格式不合规")
        if (serverUserDao.selectOne(QueryWrapper<ServerUserModel>().eq("username", username)) != null) {
            return HandUtils.handleResultByCode(HttpStatus.NOT_ACCEPTABLE, null, "用户名已存在")
        } else {
            val uid = generateUniqueUid()
            val serverUserModel = ServerUserModel()
            val result = ClientUserManage().registerUser(serverUserModel, uid, username, HandUtils.encodeStringToMD5(password), address)
            val userinfo = result["userinfo"] as Map<*, *>?
            result["token"] = tokenService.generateUserToken(userinfo!!["uid"].toString().toLong(), username)
            val token = result["token"]
            if (serverUserDao.insert(serverUserModel) > 0) {
                ConsoleUtils.printInfoLog("User Register $address $uid $token")
                HandUtils.sendGlobalMessage(socketIOServer!!, "[RE:USER:ALL]", userinfo)
                return HandUtils.handleResultByCode(HttpStatus.OK, result, "注册成功")
            } else return HandUtils.handleResultByCode(HttpStatus.INTERNAL_SERVER_ERROR, null, "注册失败")
        }
    }

    private fun loginUserScan(username: String, address: String): Map<String, Any> {
        val selectResult = serverUserDao.selectOne(QueryWrapper<ServerUserModel>().eq("username", username))
        return if (selectResult == null) {
            HandUtils.handleResultByCode(HttpStatus.NOT_ACCEPTABLE, null, "未查询到用户")
        } else userLogin(
            address = address,
            username = username,
            selectResult = selectResult
        )
    }

    fun loginUserWithQQ(qqId: String, address: String): Map<String, Any> {
        val selectResult = serverUserDao.selectOne(QueryWrapper<ServerUserModel>().eq("qq_id", qqId))
        return if (selectResult == null) {
            HandUtils.handleResultByCode(HttpStatus.NOT_ACCEPTABLE, null, "请先注册账号")
        } else userLogin(
            address = address,
            selectResult = selectResult,
            username = selectResult.username
        )
    }

    fun editForNick(uid: Long, nick: String): Map<String, Any> {
        if (nick.length > 10 || nick.length < 2) return HandUtils.handleResultByCode(HttpStatus.NOT_ACCEPTABLE, null, "昵称长度不合规")
        val serverUserModel = ServerUserModel()
        val result = ClientUserManage().updateNick(serverUserModel, uid, nick)
        if (serverUserDao.updateById(serverUserModel) > 0) {
            HandUtils.sendGlobalMessage(
                event = "[RE:USER:NICK]",
                server = socketIOServer!!,
                content = HandUtils.handleResultByCode(HttpStatus.OK, result, "修改昵称成功")
            )
            return HandUtils.handleResultByCode(HttpStatus.OK, result, "修改昵称成功")
        } else return HandUtils.handleResultByCode(HttpStatus.INTERNAL_SERVER_ERROR, result, "修改昵称失败")
    }

    fun editForAvatar(uid: Long, path: String): Map<String, Any> {
        val serverUserModel = ServerUserModel()
        val result = ClientUserManage().updateAvatar(serverUserModel, uid, path)
        if (serverUserDao.updateById(serverUserModel) > 0) {
            HandUtils.sendGlobalMessage(
                event = "[RE:USER:AVATAR]",
                server = socketIOServer!!,
                content = HandUtils.handleResultByCode(HttpStatus.OK, result, "修改头像成功")
            )
            return HandUtils.handleResultByCode(HttpStatus.OK, result, "修改头像成功")
        } else return HandUtils.handleResultByCode(HttpStatus.INTERNAL_SERVER_ERROR, result, "修改头像失败")
    }

    fun editForUserName(uid: Long, username: String): Map<String, Any> {
        if (HandUtils.isValidUsername(username)) return HandUtils.handleResultByCode(HttpStatus.NOT_ACCEPTABLE, null, "账号格式不合规")
        val serverUserModel = ServerUserModel()
        val result = ClientUserManage().updateUserName(serverUserModel, uid, username)
        if (serverUserDao.updateById(serverUserModel) > 0) {
            HandUtils.sendGlobalMessage(
                server = socketIOServer!!,
                event = "[RE:USER:USERNAME]",
                content = HandUtils.handleResultByCode(HttpStatus.OK, result, "修改用户名成功")
            )
            return HandUtils.handleResultByCode(HttpStatus.OK, result, "修改账号成功")
        } else return HandUtils.handleResultByCode(HttpStatus.INTERNAL_SERVER_ERROR, result, "修改账号失败")
    }

    fun editForPassword(uid: Long, password: String): Map<String, Any> {
        if (HandUtils.isValidPassword(password)) return HandUtils.handleResultByCode(HttpStatus.NOT_ACCEPTABLE, null, "密码格式不合规")
        val serverUserModel = ServerUserModel()
        ClientUserManage().updatePassword(serverUserModel, uid, HandUtils.encodeStringToMD5(password))
        return if (serverUserDao.updateById(serverUserModel) > 0) {
            HandUtils.handleResultByCode(HttpStatus.OK, null, "修改密码成功")
        } else HandUtils.handleResultByCode(HttpStatus.INTERNAL_SERVER_ERROR, null, "修改密码失败")
    }

    fun getUserQrcodeScanStatus(qid: String, address: String): Map<String, Any> {
        val status = tokenService.getScanStatus(qid)
            ?: return HandUtils.handleResultByCode(HttpStatus.UNAUTHORIZED, null, "二维码已过期")
        return when(status) {
            "0" -> HandUtils.handleResultByCode(HttpStatus.BAD_REQUEST, null, "等待扫码中")
            "1" -> {
                tokenService.removeScanStatus(qid)
                val targetUser = tokenService.getScanTargetUser(qid)
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

    private fun generateUniqueUid(): Long {
        var uid: Long
        do {
            uid = IDGenerator.generateRandomId(8)
        } while (serverUserDao.selectOne(QueryWrapper<ServerUserModel>().eq("uid", uid)) != null)
        return uid
    }

    private fun userLogin(selectResult: ServerUserModel, username: String, address: String): Map<String, Any> {
        val uid = selectResult.uid
        val token = tokenService.generateUserToken(selectResult.uid, username)
        ConsoleUtils.printInfoLog("User Login $address $uid $token")
        return HandUtils.handleResultByCode(HttpStatus.OK, java.util.Map.of("token", token, "userinfo", clientUserDao.selectOne(QueryWrapper<ClientUserModel>().eq("username", username))), "登录成功")
    }
}
