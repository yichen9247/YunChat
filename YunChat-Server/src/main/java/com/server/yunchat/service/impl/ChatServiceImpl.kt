package com.server.yunchat.service.impl

import com.server.yunchat.builder.data.SocketSendMessage
import com.server.yunchat.builder.model.MessageModel
import com.server.yunchat.builder.types.SendType
import com.server.yunchat.builder.utils.HandUtils
import com.server.yunchat.plugin.SpringPlugin
import com.server.yunchat.service.ChatService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import java.util.*

/**
 * @name 消息服务实现类
 * @author yichen9247
 */
@Service
class ChatServiceImpl @Autowired constructor(
    private val springPlugin: SpringPlugin,
    private val socketServiceImpl: SocketServiceImpl
): ChatService {

    /**
     * @name 发送房间消息
     * @param message 消息模型
     */
    override fun sendRoomMessage(message: MessageModel) {
        val roomPrefix = when (message.obj) {
            SendType.SEND_USER -> "user"
            SendType.SEND_GROUP -> "group"
            else -> return
        }.let { "$it-${message.tar}" }
        socketServiceImpl.sendRoomMessage(
            room = roomPrefix,
            event = "[CLIENT:MESSAGE]",
            content = HandUtils.handleResultByCode(HttpStatus.OK, message, "有新消息")
        ) // 发送房间消息
        message.content = getMessageType(message.type, message.content)
        socketServiceImpl.sendRoomMessage(
            room = "msg-$roomPrefix",
            event = "[CLIENT:NEW:MESSAGE]",
            content = HandUtils.handleResultByCode(HttpStatus.OK, message, "有新消息")
        ) // 发送消息通知
    }


    /**
     * @name 设置消息模型
     * @param uid 用户ID
     * @param message 消息数据
     * @param messageModel 消息模型
     */
    override fun setMessageModel(
        uid: Long,
        content: String,
        message: SocketSendMessage,
        messageModel: MessageModel,
    ): MessageModel {
        val time = HandUtils.formatTimeForString("yyyy-MM-dd HH:mm:ss")
        messageModel.uid = uid
        messageModel.time = time
        messageModel.content = content
        messageModel.obj = message.obj
        messageModel.tar = message.tar
        messageModel.type = message.type
        messageModel.sid = this.setMessageId(uid, message, time)
        springPlugin.validField(messageModel)
        return messageModel
    }

    /**
     * @name 生成消息ID
     * @param time 时间
     * @param message 消息模型
     */
    private fun setMessageId(uid: Long, message: SocketSendMessage, time: String): String {
        return HandUtils.encodeStringToMD5("${UUID.randomUUID()}:${message.obj}:${message.tar}:$uid:$time")
    }

    private fun getMessageType(type: String, content: String): String {
        return when (type) {
            "text" -> content.take(8)
            "file" -> "[文件消息]"
            "image" -> "[图片消息]"
            "video" -> "[视频消息]"
            "invite" -> "[分享群聊]"
            else -> "[未知消息]"
        }
    }
}