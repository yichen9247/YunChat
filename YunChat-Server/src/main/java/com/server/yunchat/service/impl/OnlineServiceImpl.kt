package com.server.yunchat.service.impl

import com.corundumstudio.socketio.SocketIOClient
import com.server.yunchat.admin.service.ServerSystemService
import com.server.yunchat.builder.data.RedisUserModel
import com.server.yunchat.builder.data.ResultModel
import com.server.yunchat.builder.data.SocketOnlineModel
import com.server.yunchat.builder.types.RedisType.ONLINE_CLIENTS_KEY
import com.server.yunchat.builder.types.RedisType.ONLINE_USERS_KEY
import com.server.yunchat.builder.types.RedisType.ROBOT_MESSAGE_KEY
import com.server.yunchat.builder.utils.HandUtils
import com.server.yunchat.plugin.TaskPlugin
import com.server.yunchat.service.OnlineService
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class OnlineServiceImpl @Autowired constructor(
    private val taskPlugin: TaskPlugin,
    private val socketServiceImpl: SocketServiceImpl,
    private val serverSystemService: ServerSystemService,
    @Qualifier("onlineUserRedisTemplate")
    private val onlineUserRedisTemplate: RedisTemplate<String, RedisUserModel>
) : OnlineService {

    @PostConstruct
    private fun init() {
        onlineUserRedisTemplate.delete(ONLINE_USERS_KEY)
        onlineUserRedisTemplate.delete(ONLINE_CLIENTS_KEY)
    }

    /**
     * @name 用户上线
     * @param uid 用户
     * @param platform 平台
     * @param client 客户端
     */
    override fun userGoOnline(client: SocketIOClient, uid: Long, platform: String) {
        onlineUserRedisTemplate.opsForHash<String, RedisUserModel>().put(
            ONLINE_USERS_KEY, client.sessionId.toString(), RedisUserModel(
                uid = uid,
                platform = platform
            )
        )
        onlineUserRedisTemplate.convertAndSend(ONLINE_USERS_KEY, "在线列表更新")
    }

    /**
     * @name 用户下线
     * @client 客户端
     */
    override fun userGoOffline(client: SocketIOClient) {
        onlineUserRedisTemplate.opsForHash<String, RedisUserModel>().delete(
            ONLINE_USERS_KEY, client.sessionId.toString()
        )
        onlineUserRedisTemplate.opsForHash<String, RedisUserModel>().delete(
            ONLINE_CLIENTS_KEY, client.sessionId.toString()
        )
        onlineUserRedisTemplate.convertAndSend(ONLINE_USERS_KEY, "在线列表更新")
    }

    /**
     * @name 客户端上线
     * @param client 客户端
     */
    override fun clientGoOnline(client: SocketIOClient) {
        val token = HandUtils.encryptString(client.sessionId.toString())
        onlineUserRedisTemplate.opsForHash<String, RedisUserModel>().put(
            ONLINE_CLIENTS_KEY, client.sessionId.toString(), RedisUserModel(
                token = token
            )
        )
        client.sendEvent("[CLIENT:TOKEN]", HandUtils.handleResultByCode(
            HttpStatus.OK, token, "临时密钥"
        ))
        onlineUserRedisTemplate.convertAndSend(ONLINE_USERS_KEY, "在线列表更新")
    }


    /**
     * @name 获取在线用户列表
     * @return List<SocketRedisUserModel>
     */
    override fun getOnlineUserList(): List<RedisUserModel> {
        val users = onlineUserRedisTemplate.opsForHash<String, RedisUserModel>().values(
            ONLINE_USERS_KEY
        ) ?: emptyList()
        val uniqueMap = mutableMapOf<Long, RedisUserModel>()

        // 策略1：保留最新的记录
        users.forEach { user ->
            uniqueMap[user.uid] = user

            // 策略2：保留最旧的记录
            // if (!uniqueMap.containsKey(user.uid)) {
            //     uniqueMap[user.uid] = user
            // }
        }
        return uniqueMap.values.toList()
    }

    /**
     * @name 发送新用户欢迎
     * @param nick 昵称
     * @param username 账号
     */
    override fun sendNewUserWelcome(nick: String, username: String) {
        taskPlugin.doDeleteRedisCache() // 清除缓存
        if (serverSystemService.getSystemKeyStatus("welcome")) {
            onlineUserRedisTemplate.convertAndSend(
                ROBOT_MESSAGE_KEY, "春风十里不如你，欢迎 @$nick（$username） 加入群聊！"
            )
        }
        socketServiceImpl.sendGlobalMessage("[RE:USER:ALL]", {}) // 全局更新用户列表
    }

    /**
     * @name 获取客户端在线状态
     * @param client 客户端
     * @return Boolean
     */
    override fun getClientOnlineStatus(client: SocketIOClient, tempToken: String): Boolean {
        val sessionId = client.sessionId.toString()
        val clientRecord = onlineUserRedisTemplate.opsForHash<String, RedisUserModel>().get(
            ONLINE_CLIENTS_KEY, sessionId
        )
        return when {
            clientRecord != null -> clientRecord.token == tempToken
            else -> false
        }
    }

    /**
     * @name 处理上线/下线事件
     * @param uid 用户
     * @param data 数据
     * @param client 客户端
     */
    override fun handleUserOnlineEvent(uid: Long, data: SocketOnlineModel, client: SocketIOClient): ResultModel {
        when (data.status) {
            0 -> this.userGoOffline(client)
            1 -> data.platform?.let { this.userGoOnline(client, uid, it) }
        }
        return HandUtils.handleResultByCode(HttpStatus.OK, null, "操作成功")
    }
}