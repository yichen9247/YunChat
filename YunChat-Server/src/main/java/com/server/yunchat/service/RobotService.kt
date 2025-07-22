package com.server.yunchat.service

import com.corundumstudio.socketio.SocketIOClient
import com.server.yunchat.builder.data.ResultModel

interface RobotService {
    fun onUserMessage(client: SocketIOClient, uid: Long, obj: Int, tar: Long, content: String)
    fun sendRobotMessage(content: String, obj: Int, tar: Long, type: String): ResultModel
}