package com.server.yunchat.socket.listener

import com.corundumstudio.socketio.AckRequest
import com.corundumstudio.socketio.SocketIOClient
import com.server.yunchat.builder.data.SocketGroupOperate
import com.server.yunchat.builder.data.SocketSearchGroup
import com.server.yunchat.builder.types.EventType
import com.server.yunchat.builder.types.UserAuthType
import com.server.yunchat.client.service.ClientGroupService
import com.server.yunchat.socket.service.SocketEventListener
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class GroupListener @Autowired constructor(
    private val clientGroupService: ClientGroupService
) {
    /**
     * @name 获取群聊列表
     */
    @SocketEventListener(event = EventType.GET_GROUP_LIST, eventClass = Any::class)
    fun searchGroupList(client: SocketIOClient, data: Any, ackSender: AckRequest, uid: Long) {
        ackSender.sendAckData(clientGroupService.getGroupList(uid))
    }

    /**
     * @name 用户加入群聊
     */
    @SocketEventListener(
        event = EventType.ADD_GROUP_JOIN,
        eventClass = SocketGroupOperate::class,
        permission = UserAuthType.USER_AUTHENTICATION
    )
    fun onJoinGroup(client: SocketIOClient, data: SocketGroupOperate, ackSender: AckRequest, uid: Long) {
        ackSender.sendAckData(clientGroupService.addGroupMember(data.gid!!, uid))
    }

    /**
     * @name 用户退出群聊
     */
    @SocketEventListener(
        event = EventType.DEL_GROUP_EXIT,
        eventClass = SocketGroupOperate::class,
        permission = UserAuthType.USER_AUTHENTICATION
    )
    fun onExitGroup(client: SocketIOClient, data: SocketGroupOperate, ackSender: AckRequest, uid: Long) {
        ackSender.sendAckData(clientGroupService.deleteGroupMember(data.gid!!, uid))
    }

    /**
     * @name 模糊搜索群聊
     */
    @SocketEventListener(
        event = EventType.SEARCH_GROUP,
        eventClass = SocketSearchGroup::class,
        permission = UserAuthType.USER_AUTHENTICATION
    )
    fun onSearchGroup(client: SocketIOClient, data: SocketSearchGroup, ackSender: AckRequest) {
        ackSender.sendAckData(clientGroupService.searchGroupList(data.name!!))
    }
}