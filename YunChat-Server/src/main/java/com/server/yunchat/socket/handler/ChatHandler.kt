package com.server.yunchat.socket.handler

import com.corundumstudio.socketio.SocketIOClient
import com.server.yunchat.builder.data.ResultModel
import com.server.yunchat.builder.data.SocketGetMessageList
import com.server.yunchat.builder.data.SocketSendMessage
import com.server.yunchat.builder.utils.HandUtils
import com.server.yunchat.builder.utils.ResultUtils
import com.server.yunchat.client.service.ClientChatService
import com.server.yunchat.plugin.SpringPlugin
import com.server.yunchat.service.impl.AuthServiceImpl
import com.server.yunchat.service.impl.RobotServiceImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class ChatHandler @Autowired constructor(
    private val springPlugin: SpringPlugin,
    private val authServiceImpl: AuthServiceImpl,
    private val robotServiceImpl: RobotServiceImpl,
    private val clientChatService: ClientChatService
) {
    fun handleSendMessage(uid: Long, client: SocketIOClient, data: SocketSendMessage): ResultModel {
        return try {
            var content = data.content!!
            springPlugin.validField(data)
            if (!data.type.equals("code")) {
                content = HandUtils.stripHtmlTagsForString(content)
            }
            authServiceImpl.validChatMessage(uid, data.obj!!, data.tar!!)
            val result = clientChatService.sendChatMessage(uid, content, data)
            if (result.code == HttpStatus.OK.value() && !data.type.equals("code")) {
                robotServiceImpl.onUserMessage(
                    uid = uid,
                    obj = data.obj,
                    tar = data.tar,
                    client = client,
                    content = content
                )
            }
            return result
        } catch (e: Exception) {
            HandUtils.handleResultByCode(HttpStatus.BAD_REQUEST, null, e.message.toString())
        }
    }

     fun getMessageList(data: SocketGetMessageList): ResultModel {
         return if (data.obj == null || data.tar == null) {
             ResultUtils.printParamMessage()
         } else clientChatService.getMessageList(data.obj, data.tar)
     }
}