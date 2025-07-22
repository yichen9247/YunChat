package com.server.yunchat.service

interface SocketService {
    fun sendGlobalMessage(event: String, content: Any)
    fun sendRoomMessage(room: String, event: String, content: Any)
}