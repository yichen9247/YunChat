package com.server.yunchat.builder.data

// 房间加入/退出
data class SocketRoomModel(
    val data: Int? = null,
    val type: String? = null
)

// 用户上线/下线
data class SocketOnlineModel(
    val status: Int? = null,
    val platform: String? = null
)

// 消息房间加入/退出
data class SocketMsgRoomModel(
    val type: String? = null,
    val list: List<Int>? = null
)

// 用户登录/注册
data class SocketUserLogin(
    val data: String? = null,
    val tempToken: String? = null
)

data class SocketScanStatus(
    val qid: String? = null,
    val tempToken: String? = null
)

data class SocketSearchGroup(
    val name: String? = null
)

data class SocketUserReport(
    val sid: String? = null,
    val reason: String? = null,
    val reportedId: Long? = null
)

data class SocketSystemConfig(
    val name: String? = null,
    val value: String? = null
)

data class SocketGroupOperate(
    val gid: Int? = null
)

data class SocketSendMessage(
    val obj: Int?= null,
    val tar: Long? = null,
    val type: String? = null,
    var content: String? = null
)

data class SocketGetMessageList(
    val obj: Int?= null,
    val tar: Long? = null
)