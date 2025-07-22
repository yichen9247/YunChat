package com.android.yunchat.core

import io.socket.client.Socket

object SocketConstants {
    object Events {
        const val CONNECT = Socket.EVENT_CONNECT
        const val DISCONNECT = Socket.EVENT_DISCONNECT

        const val JOIN_ROOM = "[JOIN:ROOM]"
        const val SEND_MESSAGE = "[SEND:MESSAGE]"
        const val ONLINE_LOGIN = "[ONLINE:LOGIN]"
        const val GET_USER_LIST = "[GET:USER:LIST]"
        const val JOIN_MSG_ROOM = "[JOIN:MSG:ROOM]"
        const val RECE_RE_USER_ALL = "[RE:USER:ALL]"
        const val RECE_RE_USER_NICK = "[RE:USER:NICK]"
        const val SEND_AI_MESSAGE = "[SEND:AI:MESSAGE]"
        const val RESET_AI_MESSAGE = "[RESET:AI:MESSAGE]"
        const val GET_MESSAGE_LIST = "[GET:MESSAGE:LIST]"
        const val RECE_RE_USER_AVATAR = "[RE:USER:AVATAR]"
        const val RECE_CLIENT_MESSAGE = "[CLIENT:MESSAGE]"
        const val RECE_AI_CREATE_MESSAGE = "[AI:CHAT:CREATE:MESSAGE]"
    }
}