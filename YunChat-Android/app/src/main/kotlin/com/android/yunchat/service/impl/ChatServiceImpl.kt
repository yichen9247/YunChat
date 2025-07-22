package com.android.yunchat.service.impl

import com.android.yunchat.config.AppConfig
import com.android.yunchat.service.ChatService
import com.android.yunchat.service.instance.encryptServiceInstance
import io.socket.client.IO
import io.socket.engineio.client.transports.WebSocket

/**
 * @name 聊天服务实现类
 * @author yichen9247
 */
class ChatServiceImpl: ChatService {
    /**
     * @name 创建Socket配置
     * @return IO.Options
     */
    override fun createSocketOptions(): IO.Options {
        return IO.Options().apply {
            timeout = 5000
            reconnection = true
            reconnectionDelay = 1000
            randomizationFactor = 0.5
            auth = createAuthHeaders()
            transports = arrayOf(WebSocket.NAME)
            reconnectionAttempts = Int.MAX_VALUE
        }
    }

    /**
     * @name 创建Socket认证头
     * @return SocketAuthHeaderModel
     */
    override fun createAuthHeaders(): Map<String, String> {
        return mapOf(
            "version" to AppConfig.socketVersion,
            "authorization" to encryptServiceInstance.encryptAuthorization()
        )
    }
}