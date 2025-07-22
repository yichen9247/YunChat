package com.server.yunchat.service.impl

import com.server.yunchat.builder.utils.GlobalService
import com.server.yunchat.service.SocketService
import org.springframework.stereotype.Service

/**
 * @name 通讯服务实现类
 * @author yichen9247
 */
@Service
class SocketServiceImpl: SocketService {
    /**
     * @name 发送全局消息
     * @param event 事件名称
     * @param content 消息内容
     */
    override fun sendGlobalMessage(event: String, content: Any) {
        GlobalService.socketIOServer?.apply {
            val broadcastOperations = this.broadcastOperations
            broadcastOperations.sendEvent(event, content)
        }
    }

    /**
     * @name 发送房间消息
     * @param room 房间ID
     * @param event 事件名称
     * @param content 消息内容
     */
    override fun sendRoomMessage(room: String, event: String, content: Any) {
        sendRoomMessageApi(room, event, content)
    }

    /**
     * @name 发送房间消息
     * @param gid 房间ID
     * @param event 事件名称
     * @param content 消息内容
     */
    private fun sendRoomMessageApi(room: String, event: String, content: Any) {
        GlobalService.socketIOServer?.apply {
            this.getRoomOperations(room).sendEvent(event, content)
        }
    }
}