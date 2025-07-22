package com.server.yunchat.service.impl

import com.server.yunchat.admin.service.ServerSystemService
import com.server.yunchat.builder.dao.GroupDao
import com.server.yunchat.builder.types.SendType
import com.server.yunchat.builder.types.UserAuthType
import com.server.yunchat.client.dao.ClientUserDao
import com.server.yunchat.client.mod.ClientUserModel
import com.server.yunchat.service.AuthService
import com.server.yunchat.service.exce.AuthValidException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * @name 权限服务实现类
 * @author yichen9247
 */
@Service
class AuthServiceImpl @Autowired constructor(
    private val groupDao: GroupDao,
    private val clientUserDao: ClientUserDao,
    private val redisServiceImpl: RedisServiceImpl,
    private val serverSystemService: ServerSystemService
): AuthService {
    /**
     * @name 验证聊天消息权限
     * @param uid 用户id
     * @param tar 目标id
     * @param obj 消息类型
     */
    override fun validChatMessage(uid: Long, obj: Int, tar: Long) {
        validMessageChatAuth(uid)
        when(obj) {
            SendType.SEND_USER -> validUserChatAuth(uid, tar)
            SendType.SEND_GROUP -> validGroupChatAuth(uid, tar.toInt())
        }
    }

    /**
     * @name 验证公有消息权限
     * @param uid 用户id
     */
    private fun validMessageChatAuth(uid: Long) {
        val user = clientUserDao.selectById(uid)
        if (!redisServiceImpl.validUserMessageCache(uid)) {
            throw AuthValidException("发送频率太快了")
        }
        if (user.status == UserAuthType.TABOO_STATUS) {
            throw AuthValidException("你正在被禁言中")
        }
        if (getGlobalTabooStatus(user)) {
            throw AuthValidException("系统正在禁言中")
        }
    }

    /**
     * @name 验证私聊消息权限
     * @param uid 用户id
     * @param tar 目标id
     */
    private fun validUserChatAuth(uid: Long, tar: Long) {

    }

    /**
     * @name 验证群聊消息权限
     * @param uid 用户id
     * @param gid 群组id
     */
    private fun validGroupChatAuth(uid: Long, gid: Int) {
        val user = clientUserDao.selectById(uid)
        if (!groupDao.getIsUserInGroup(gid, uid)) {
            throw AuthValidException("你已不在群聊中")
        }
        if (getGroupTabooStatus(user, gid)) {
            throw AuthValidException("群聊正在禁言中")
        }
    }

    /**
     * @name 获取全局禁言状态
     * @param user 用户
     */
    override fun getGlobalTabooStatus(user: ClientUserModel): Boolean {
        val status = serverSystemService.getSystemKeyStatus("taboo")
        return status && user.permission != UserAuthType.ADMIN_AUTHENTICATION
    }

    /**
     * @name 获取群组禁言状态
     * @param user 用户
     */
    override fun getGroupTabooStatus(user: ClientUserModel, gid: Int): Boolean {
        val group = groupDao.getGroupInfo(gid)
        return group?.status == UserAuthType.TABOO_STATUS && user.permission != UserAuthType.ADMIN_AUTHENTICATION
    }

    /**
     * @name 验证注册开关状态
     */
    override fun validRegisterStatus() {
        if (!serverSystemService.getSystemKeyStatus("register")) {
            throw AuthValidException("注册功能已关闭")
        }
    }
}