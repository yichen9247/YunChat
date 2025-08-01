package com.server.yunchat.socket.handler

import com.corundumstudio.socketio.SocketIOClient
import com.server.yunchat.builder.data.ResultModel
import com.server.yunchat.builder.data.SocketMsgRoomModel
import com.server.yunchat.builder.data.SocketRoomModel
import com.server.yunchat.builder.utils.HandUtils.handleResultByCode
import com.server.yunchat.builder.utils.ResultUtils
import com.server.yunchat.client.service.ClientGroupService
import com.server.yunchat.service.impl.GroupServiceImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class RoomHandler @Autowired constructor(
    private val groupServiceImpl: GroupServiceImpl,
    private val clientGroupService: ClientGroupService
) {

    private companion object {
        const val DEFAULT_GROUP_ID = 1
        const val GROUP_ROOM_PREFIX = "group-"
        const val MSG_GROUP_ROOM_PREFIX = "msg-group-"
    }

    fun handleLeaveRoom(data: SocketRoomModel, client: SocketIOClient): ResultModel {
        if (data.type.isNullOrEmpty() || data.data == null) {
            return ResultUtils.printParamMessage()
        }

        return when (data.type) {
            "group" -> leaveGroupRoom(data.data, client)
            else -> handleResultByCode(HttpStatus.BAD_REQUEST, null, "不支持的房间类型")
        }
    }

    fun handleJoinRoom(uid: Long, data: SocketRoomModel, client: SocketIOClient): ResultModel {
        if (data.type.isNullOrEmpty() || data.data == null) {
            return ResultUtils.printParamMessage()
        }

        return when (data.type) {
            "group" -> joinGroupRoom(uid, data.data, client)
            else -> handleResultByCode(HttpStatus.BAD_REQUEST, null, "不支持的房间类型")
        }
    }

    fun handleJoinMsgRoom(uid: Long, data: SocketMsgRoomModel, client: SocketIOClient): ResultModel {
        if (data.type.isNullOrEmpty() || data.list.isNullOrEmpty()) {
            return ResultUtils.printParamMessage()
        }

        return when (data.type) {
            "group" -> joinMsgGroupRoom(uid, data.list, client)
            else -> handleResultByCode(HttpStatus.BAD_REQUEST, null, "不支持的房间类型")
        }
    }

    fun handleLeaveMsgRoom(data: SocketMsgRoomModel, client: SocketIOClient): ResultModel {
        if (data.type.isNullOrEmpty() || data.list.isNullOrEmpty()) {
            return ResultUtils.printParamMessage()
        }

        return when (data.type) {
            "group" -> leaveMsgGroupRoom(data.list, client)
            else -> handleResultByCode(HttpStatus.BAD_REQUEST, null, "不支持的房间类型")
        }
    }

    private fun leaveGroupRoom(gid: Int, client: SocketIOClient): ResultModel {
        client.leaveRoom(groupRoomName(gid))
        return handleResultByCode(HttpStatus.OK, null, "离开房间成功")
    }

    private fun joinGroupRoom(uid: Long, gid: Int, client: SocketIOClient): ResultModel {
        if (!isUserInGroupOrDefault(gid, uid)) {
            return handleResultByCode(HttpStatus.BAD_REQUEST, null, "用户不在该群组")
        }

        client.joinRoom(groupRoomName(gid))
        val groupInfo = groupServiceImpl.getGroupInfo(gid)
        return handleResultByCode(HttpStatus.OK, groupInfo, "加入房间成功")
    }

    private fun joinMsgGroupRoom(uid: Long, list: List<Int>, client: SocketIOClient): ResultModel {
        val invalidGroup = list.find { !isUserInGroupOrDefault(it, uid) }
        if (invalidGroup != null) {
            return handleResultByCode(HttpStatus.BAD_REQUEST, null, "用户不在群聊中")
        }
        list.forEach { client.joinRoom(msgGroupRoomName(it)) }
        return handleResultByCode(HttpStatus.OK, null, "加入消息房间成功")
    }

    private fun leaveMsgGroupRoom(list: List<Int>, client: SocketIOClient): ResultModel {
        list.forEach { client.leaveRoom(msgGroupRoomName(it)) }
        return handleResultByCode(HttpStatus.OK, null, "离开消息房间成功")
    }

    private fun groupRoomName(gid: Int) = "$GROUP_ROOM_PREFIX$gid"
    private fun msgGroupRoomName(gid: Int) = "$MSG_GROUP_ROOM_PREFIX$gid"
    private fun isUserInGroupOrDefault(gid: Int, uid: Long): Boolean {
        return gid == DEFAULT_GROUP_ID || clientGroupService.getIsUserInGroup(gid, uid)
    }
}