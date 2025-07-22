package com.server.yunchat.service

import com.corundumstudio.socketio.SocketIOClient
import jakarta.servlet.http.HttpServletRequest

interface ClientService {
    fun getSocketClientIp(client: SocketIOClient): String
    fun getHttpClientIp(request: HttpServletRequest): String
    fun getSocketClientVersion(client: SocketIOClient): String
}