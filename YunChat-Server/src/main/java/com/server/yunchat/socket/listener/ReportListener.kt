package com.server.yunchat.socket.listener

import com.corundumstudio.socketio.AckRequest
import com.corundumstudio.socketio.SocketIOClient
import com.server.yunchat.builder.data.SocketUserReport
import com.server.yunchat.builder.types.EventType
import com.server.yunchat.builder.types.UserAuthType
import com.server.yunchat.socket.handler.ReportHandler
import com.server.yunchat.socket.service.SocketEventListener
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ReportListener @Autowired constructor(private val reportHandler: ReportHandler) {
    @SocketEventListener(
        event = EventType.REPORT_USER,
        eventClass = SocketUserReport::class,
        permission = UserAuthType.USER_AUTHENTICATION
    )
    fun reportUser(client: SocketIOClient, data: SocketUserReport, ackSender: AckRequest, uid: Long) {
        ackSender.sendAckData(reportHandler.handleReport(data, uid))
    }
}