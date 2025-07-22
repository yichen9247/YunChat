package com.server.yunchat.service.impl

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.server.yunchat.admin.dao.ServerUserDao
import com.server.yunchat.admin.mod.ServerUserModel
import com.server.yunchat.builder.model.MemberModel
import com.server.yunchat.builder.types.RedisType.USER_INFO_KEY
import com.server.yunchat.builder.types.UserAuthType
import com.server.yunchat.builder.utils.HandUtils
import com.server.yunchat.client.dao.ClientUserDao
import com.server.yunchat.client.mod.ClientUserModel
import com.server.yunchat.plugin.SpringPlugin
import com.server.yunchat.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.util.*
import java.util.concurrent.ThreadLocalRandom

/**
 * @name 用户服务实现类
 * @author yichen9247
 */
@Service
class UserServiceImpl @Autowired constructor(
    private val springPlugin: SpringPlugin,
    private val serverUserDao: ServerUserDao,
    private val clientUserDao: ClientUserDao,
    @Qualifier("userInfoRedisTemplate")
    private val userInfoRedisTemplate: RedisTemplate<String, ClientUserModel>
): UserService {
    /**
     * @name 获取随机头像
     * @return String
     */
    override fun getRandomAvatar(): String {
        return "0/default/" + Random().nextInt(7) + ".png"
    }

    /**
     * @name 获取是否管理
     * @param uid 用户id
     * @return Boolean
     */
    override fun getUserIsAdmin(uid: Long): Boolean {
        return clientUserDao.selectById(uid)?.let {
            it.permission == UserAuthType.ADMIN_AUTHENTICATION
        } ?: false
    }

    /**
     * @name 获取是否机器
     * @param uid 用户id
     * @return Boolean
     */
    override fun getUserIsRobot(uid: Long): Boolean {
        return clientUserDao.selectById(uid)?.let {
            it.permission == UserAuthType.ROBOT_AUTHENTICATION
        } ?: false
    }

    /**
     * @name 获取AI权限
     * @param uid 用户id
     * @return Boolean
     */
    override fun getUserAiAuth(uid: Long): Boolean {
        return clientUserDao.selectById(uid)?.let {
            it.aiAuth == 1
        } ?: false
    }

    /**
     * @name 设置AI权限
     * @param uid 用户id
     * @param status 状态
     * @return Boolean
     */
    override fun setUserAiAuth(
        uid: Long,
        status: Boolean
    ) {
        clientUserDao.selectById(uid)?.let {
            it.aiAuth = if (status) 1 else 0
            clientUserDao.updateById(it)
        }
    }

    /**
     * @name 获取用户信息
     * @param uid 用户id
     * @description 从缓存中获取用户信息，若缓存中不存在则从数据库中获取并缓存
     */
    override fun getClientUserInfo(uid: Long): ClientUserModel? {
        val redisCache = userInfoRedisTemplate.opsForHash<String, ClientUserModel>()
            .get(USER_INFO_KEY, uid.toString())
        return redisCache ?: queryClientUserInfo(uid)?.also { currentUser ->
            userInfoRedisTemplate.opsForHash<String, ClientUserModel>()
                .put(USER_INFO_KEY, uid.toString(), currentUser)
        }
    }

    /**
     * @name 修改用户信息
     */
    override fun setUserInfo(
        uid: Long,
        nick: String,
        robot: Boolean,
        avatar: String,
        serverUserModel: ServerUserModel
    ): ServerUserModel {
        serverUserModel.uid = uid
        serverUserModel.nick = nick
        serverUserModel.avatar = avatar
        springPlugin.validField(serverUserModel)
        serverUserDao.selectList(
            QueryWrapper<ServerUserModel>().eq(
                "permission", UserAuthType.ROBOT_AUTHENTICATION
            )
        ).forEach {
            it.permission = UserAuthType.USER_AUTHENTICATION
            serverUserDao.updateById(it)
        }
        serverUserModel.permission =
            if (robot)
                UserAuthType.ROBOT_AUTHENTICATION
            else
                UserAuthType.USER_AUTHENTICATION
        return serverUserModel
    }

    /**
     * @name 生成用户账号
     * @return Long
     */
    override fun getRegisterUid(): Long {
        val yearPrefix = "%02d".format(Calendar.getInstance()[Calendar.YEAR] % 100)
        val randomSuffix = ThreadLocalRandom.current()
            .nextInt(100_000_000)
            .toString()
            .padStart(8, '0')
        return (yearPrefix + randomSuffix).toLong()
    }

    /**
     * @name 注册用户账号
     * @param username 账号
     * @param password 密码
     * @return ServerUserModel
     */
    override fun setRegisterUser(
        username: String,
        password: String,
        memberModel: MemberModel,
        serverUserModel: ServerUserModel
    ): ServerUserModel {
        val uid = getRegisterUid()
        memberModel.gid = 1
        memberModel.uid = uid
        serverUserModel.uid = uid
        serverUserModel.nick = "热心网友"
        serverUserModel.username = username
        serverUserModel.password = password
        serverUserModel.avatar = getRandomAvatar()
        springPlugin.validField(serverUserModel) // 验证注册模型
        serverUserModel.password = HandUtils.encodeStringToMD5(password)
        return serverUserModel
    }

    /**
     * @name 设置用户状态
     * @param uid 用户id
     * @param status 状态
     * @param serverUserModel 数据模型
     */
    override fun setUserStatus(
        uid: Long,
        status: Int,
        serverUserModel: ServerUserModel
    ): ServerUserModel {
        serverUserModel.uid = uid
        serverUserModel.status = status
        return serverUserModel
    }

    /**
     * @name 设置用户昵称
     * @param uid 用户id
     * @param nick 昵称
     * @param serverUserModel 数据模型
     */
    override fun setUserNick(
        uid: Long,
        nick: String,
        serverUserModel: ServerUserModel
    ): Map<String, Any> {
        serverUserModel.uid = uid
        serverUserModel.nick = nick
        springPlugin.validField(serverUserModel)
        return mapOf(
            "uid" to serverUserModel.uid,
            "nick" to serverUserModel.nick,
        )
    }

    /**
     * @name 设置用户头像
     * @param uid 用户id
     * @param path 路径
     * @param serverUserModel 数据模型
     */
    override fun setUserAvatar(
        uid: Long,
        path: String,
        serverUserModel: ServerUserModel
    ): Map<String, Any> {
        serverUserModel.uid = uid
        serverUserModel.avatar = path
        return mapOf(
            "uid" to serverUserModel.uid,
            "avatar" to serverUserModel.avatar,
        )
    }

    /**
     * @name 设置用户密码
     * @param uid 用户id
     * @param password 密码
     * @param serverUserModel 数据模型
     */
    override fun setUserPassword(
        uid: Long,
        password: String,
        serverUserModel: ServerUserModel
    ) {
        serverUserModel.uid = uid
        serverUserModel.password = password
        springPlugin.validField(serverUserModel)
        serverUserModel.password = HandUtils.encodeStringToMD5(password)
    }

    /**
     * @name 设置用户绑定
     * @param uid 用户id
     * @param qqId QQ账号ID
     */
    override fun setUserQqId(
        uid: Long,
        qqId: String?,
        serverUserModel: ServerUserModel
    ) {
        serverUserModel.uid = uid
        serverUserModel.qqId = qqId
    }

    /**
     * @name 查询用户信息
     * @param uid 用户id
     */
    private fun queryClientUserInfo(uid: Long): ClientUserModel? {
        return try {
            clientUserDao.selectById(uid)
        } catch (e: Exception) {
            null
        }
    }
}