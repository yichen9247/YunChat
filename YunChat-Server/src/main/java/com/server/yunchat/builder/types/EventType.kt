package com.server.yunchat.builder.types

object EventType {
    //----------- REC:CLIENT -----------//
    const val CLIENT_INIT = "[CLIENT:INIT]" // 初始化
    const val CLIENT_PING = "[CLIENT:PING]" // 心跳机
    const val ONLINE_LOGIN = "[ONLINE:LOGIN]" // 在线登录

    //----------- REC:AUTH -----------//
    const val USER_LOGIN = "[USER:LOGIN]" // 登录
    const val USER_REGISTER = "[USER:REGISTER]" // 注册
    const val USER_SCAN_LOGIN = "[USER:SCAN:LOGIN]" // 扫码登录
    const val USER_SCAN_LOGIN_STATUS = "[USER:SCAN:LOGIN:STATUS]" // 扫码登录状态

    //----------- REC:EDIT -----------//
    const val EDIT_USER_NICK = "[EDIT:USER:NICK]" // 修改昵称
    const val EDIT_USER_AVATAR = "[EDIT:USER:AVATAR]" // 修改头像
    const val EDIT_USER_PASSWORD = "[EDIT:USER:PASSWORD]" // 修改密码

    //----------- REC:CHAT -----------//
    const val SEND_MESSAGE = "[SEND:MESSAGE]" // 发送消息
    const val SEND_AI_MESSAGE = "[SEND:AI:MESSAGE]" // 发送AI消息
    const val GET_MESSAGE_LIST = "[GET:MESSAGE:LIST]" // 获取消息列表
    const val RESET_AI_MESSAGE = "[RESET:AI:MESSAGE]" // 重置AI消息

    // ----------- REC:REPORT -----------//
    const val REPORT_USER = "[REPORT:USER]" // 举报用户

    // ----------- REC:SEARCH -----------//
    const val SEARCH_GROUP = "[SEARCH:GROUP]" // 搜索群组
    const val SEARCH_NOTICE = "[SEARCH:NOTICE]" // 搜索公告
    const val SEARCH_USER_ALL = "[SEARCH:USER:ALL]" // 搜索用户

    // ----------- REC:ROOM -----------//
    const val JOIN_ROOM = "[JOIN:ROOM]" // 加入房间
    const val LEAVE_ROOM = "[LEAVE:ROOM]" // 离开房间
    const val JOIN_MSG_ROOM = "[JOIN:MSG:ROOM]" // 加入消息房间
    const val LEAVE_MSG_ROOM = "[LEAVE:MSG:ROOM]" // 离开消息房间

    // ----------- REC:GROUP -----------//
    const val GET_GROUP_LIST = "[GET:GROUP:LIST]" // 获取群组
    const val DEL_GROUP_EXIT = "[DEL:GROUP:EXIT]" // 退出群组
    const val ADD_GROUP_JOIN = "[ADD:GROUP:JOIN]" // 加入群组

    // ----------- REC:USER -----------//
    const val GET_USER_LIST = "[GET:USER:LIST]" // 获取用户

    // ----------- REC:CONFIG -----------//
    const val GET_SYSTEM_CONFIG = "[GET:SYSTEM:CONFIG]" // 获取系统配置
    const val SET_SYSTEM_CONFIG_VALUE = "[SET:SYSTEM:CONFIG:VALUE]" // 设置系统配置

    // ----------- REC:ADMIN -----------//
    const val ADMIN_GET_SYSTEM_LOGS = "[GET:ADMIN:SYSTEM:LOGS]"
    const val ADMIN_GET_DASHBOARD_DATA = "[GET:ADMIN:DASH:DATA]"
    const val ADMIN_DELETE_SYSTEM_LOGS = "[DEL:ADMIN:SYSTEM:LOGS]"
    const val ADMIN_CLEAR_HISTORY = "[RE:HISTORY:CLEAR]"
    const val ADMIN_GET_USER_LIST = "[GET:ADMIN:USER:LIST]"
    const val ADMIN_DELETE_USER = "[DEL:ADMIN:USER]"
    const val ADMIN_DELETE_CHAT = "[DEL:ADMIN:CHAT]"
    const val ADMIN_DELETE_GROUP = "[DEL:ADMIN:GROUP]"
    const val ADMIN_DELETE_REPORT = "[DEL:ADMIN:REPORT]"
    const val ADMIN_DELETE_NOTICE = "[DEL:ADMIN:NOTICE]"
    const val ADMIN_DELETE_UPLOAD = "[DEL:ADMIN:UPLOAD]"
    const val ADMIN_DELETE_MEMBER = "[DEL:ADMIN:MEMBER]"
    const val ADMIN_ADD_GROUP = "[ADD:ADMIN:GROUP]"
    const val ADMIN_ADD_NOTICE = "[ADD:ADMIN:NOTICE]"
    const val ADMIN_GET_LOGIN_LIST = "[GET:ADMIN:LOGIN:LIST]"
    const val ADMIN_GET_NOTICE_LIST = "[GET:ADMIN:NOTICE:LIST]"
    const val ADMIN_GET_CHAT_LIST = "[GET:ADMIN:CHAT:LIST]"
    const val ADMIN_GET_GROUP_LIST = "[GET:ADMIN:GROUP:LIST]"
    const val ADMIN_GET_REPORT_LIST = "[GET:ADMIN:REPORT:LIST]"
    const val ADMIN_GET_UPLOAD_LIST = "[GET:ADMIN:UPLOAD:LIST]"
    const val ADMIN_GET_CHAT_CONTENT = "[GET:ADMIN:CHAT:CONTENT]"
    const val ADMIN_UPDATE_GROUP = "[SET:ADMIN:GROUP]"
    const val ADMIN_UPDATE_NOTICE = "[SET:ADMIN:NOTICE]"
    const val ADMIN_UPDATE_USER = "[SET:ADMIN:USER]"
    const val ADMIN_UPDATE_USER_PASSWORD = "[SET:ADMIN:USER:PASSWORD]"
    const val ADMIN_UPDATE_GROUP_STATUS = "[SET:ADMIN:GROUP:STATUS]"
    const val ADMIN_UPDATE_USER_STATUS = "[SET:ADMIN:USER:STATUS]"
}