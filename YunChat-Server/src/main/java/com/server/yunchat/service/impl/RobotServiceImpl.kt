package com.server.yunchat.service.impl

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.corundumstudio.socketio.SocketIOClient
import com.server.yunchat.admin.dao.ServerUserDao
import com.server.yunchat.admin.mod.ServerUserModel
import com.server.yunchat.builder.data.ResultModel
import com.server.yunchat.builder.data.SocketSendMessage
import com.server.yunchat.builder.types.UserAuthType
import com.server.yunchat.builder.utils.HandUtils
import com.server.yunchat.client.service.ClientChatService
import com.server.yunchat.service.RobotService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

/**
 * @name 机器服务实现类
 * @author yichen9247
 */
@Service
class RobotServiceImpl @Autowired constructor(
    private val serverUserDao: ServerUserDao,
    private val userServiceImpl: UserServiceImpl,
    private val clientServiceImpl: ClientServiceImpl,
    private val clientChatService: ClientChatService,
    private val requestServiceImpl: RequestServiceImpl
): RobotService {
    companion object {
        private val HELP_COMMANDS = """
            =======yunchat=======<br/>
            userinfo：用户信息<br/>
            hitokoto：获取一言<br/>
            weibo-hot：微博热搜<br/>
            bilibili-hot：Bilibili热搜<br/>
            ====================""".trimIndent()

        private val commandList: ArrayList<String?> = object : ArrayList<String?>() {
            init {
                add("yun help") // 使用帮助
                add("yun userinfo") // 用户信息
                add("yun hitokoto") // 获取一言
                add("yun apply-ai") // 开启权限
                add("yun weibo-hot") // 微博热搜
                add("yun bilibili-hot") // Bilibili热搜
            }
        }
    }

    override fun onUserMessage(client: SocketIOClient, uid: Long, obj: Int, tar: Long, content: String) {
        Thread.sleep(500)
        val data = when(content) {
            commandList[0] -> HELP_COMMANDS
            commandList[1] -> getUserBasicInfo(client, uid)
            commandList[2] -> requestServiceImpl.getHitokoto()
            commandList[3] -> applyAiAuthorization(uid)
            commandList[4] -> requestServiceImpl.getWeiboHotSearch()
            commandList[5] -> requestServiceImpl.getBilibiliHotSearch()
            else -> null
        }
        if (data != null) sendRobotMessage(data, obj, tar, "text")
    }

    /**
     * @name 获取用户基本信息
     * @param uid 用户ID
     * @param client 客户端
     */
    private fun getUserBasicInfo(client: SocketIOClient, uid: Long): String {
        val clientUserModel = userServiceImpl.getClientUserInfo(uid)
        return """
            账号：${clientUserModel?.username}<br/>
            昵称：${clientUserModel?.nick}<br/>
            注册：${clientUserModel?.regTime}<br/>
            归属：${requestServiceImpl.getAttributionByIp(
            clientServiceImpl.getSocketClientIp(client)
        )}
        """.trimIndent()
    }

    /**
     * @name 申请AI能力权限
     * @param uid 用户ID
     */
    private fun applyAiAuthorization(uid: Long): String {
        return if (!userServiceImpl.getUserAiAuth(uid)) {
            userServiceImpl.setUserAiAuth(uid, true)
            "已为您开启AI能力"
        } else "你已拥有AI能力，无需重复开启"
    }

    /**
     * @name 发送机器人消息
     * @param obj 对象
     * @param tar 目标
     * @param type 类型
     * @param content 内容
     */
    override fun sendRobotMessage(content: String, obj: Int, tar: Long, type: String): ResultModel {
        serverUserDao.selectOne(QueryWrapper<ServerUserModel>().eq("permission", UserAuthType.ROBOT_AUTHENTICATION))?.apply {
            return clientChatService.sendChatMessage(
                uid = this.uid,
                content = content,
                message = SocketSendMessage(obj = obj, tar = tar, type = type)
            )
        }
        return HandUtils.handleResultByCode(HttpStatus.INTERNAL_SERVER_ERROR, null, "机器人不存在")
    }
}