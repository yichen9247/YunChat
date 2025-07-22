package com.server.yunchat.builder.utils

import com.corundumstudio.socketio.SocketIOServer
import com.server.yunchat.builder.props.YunChatProps
import lombok.Setter
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service

@Setter @Component @Service
object GlobalService {
    var yunchatProps: YunChatProps? = null
    var socketIOServer: SocketIOServer? = null
}