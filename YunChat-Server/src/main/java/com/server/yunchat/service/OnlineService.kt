package com.server.yunchat.service

import com.corundumstudio.socketio.SocketIOClient
import com.server.yunchat.builder.data.RedisUserModel
import com.server.yunchat.builder.data.ResultModel
import com.server.yunchat.builder.data.SocketOnlineModel

interface OnlineService {
    fun userGoOffline(client: SocketIOClient)
    fun clientGoOnline(client: SocketIOClient)
    fun getOnlineUserList(): List<RedisUserModel>
    fun sendNewUserWelcome(nick: String, username: String)
    fun userGoOnline(client: SocketIOClient, uid: Long, platform: String)
    fun getClientOnlineStatus(client: SocketIOClient, tempToken: String): Boolean
    fun handleUserOnlineEvent(uid: Long, data: SocketOnlineModel, client: SocketIOClient): ResultModel
}