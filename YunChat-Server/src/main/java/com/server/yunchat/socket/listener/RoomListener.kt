package com.server.yunchat.socket.listener

import com.corundumstudio.socketio.AckRequest
import com.corundumstudio.socketio.SocketIOClient
import com.server.yunchat.builder.data.SocketMsgRoomModel
import com.server.yunchat.builder.data.SocketRoomModel
import com.server.yunchat.builder.types.EventType
import com.server.yunchat.socket.handler.RoomHandler
import com.server.yunchat.socket.service.SocketEventListener
import org.springframework.stereotype.Service

@Service
class RoomListener(private val roomHandler: RoomHandler) {
    @SocketEventListener(event = EventType.JOIN_ROOM, eventClass = SocketRoomModel::class)
    fun joinRoom(client: SocketIOClient, data: SocketRoomModel, ackSender: AckRequest, uid: Long) {
        ackSender.sendAckData(roomHandler.handleJoinRoom(uid, data, client))
    }

    @SocketEventListener(event = EventType.LEAVE_ROOM, eventClass = SocketRoomModel::class)
    fun leaveRoom(client: SocketIOClient, data: SocketRoomModel, ackSender: AckRequest) {
        ackSender.sendAckData(roomHandler.handleLeaveRoom(data, client))
    }

    @SocketEventListener(event = EventType.JOIN_MSG_ROOM, eventClass = SocketMsgRoomModel::class)
    fun joinMsgRoom(client: SocketIOClient, data: SocketMsgRoomModel, ackSender: AckRequest, uid: Long) {
        ackSender.sendAckData(roomHandler.handleJoinMsgRoom(uid, data, client))
    }

    @SocketEventListener(event = EventType.LEAVE_MSG_ROOM, eventClass = SocketMsgRoomModel::class)
    fun leaveMsgRoom(client: SocketIOClient, data: SocketMsgRoomModel, ackSender: AckRequest) {
        ackSender.sendAckData(roomHandler.handleLeaveMsgRoom(data, client))
    }
}