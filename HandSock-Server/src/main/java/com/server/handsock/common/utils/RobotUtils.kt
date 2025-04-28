package com.server.handsock.common.utils

import com.server.handsock.client.service.ClientChatService
import com.server.handsock.client.service.ClientUserService
import com.server.handsock.common.types.UserAuthType
import com.server.handsock.service.ClientService
import org.springframework.http.HttpStatus

object RobotUtils {
    fun sendRobotMessage(
        gid: Long,
        content: String,
        address: String,
        clientService: ClientService,
        clientUserService: ClientUserService,
        clientChatService: ClientChatService
    ): Map<String, Any> {
        val robotUser = clientUserService.robotInnerStatus()
        if (robotUser.status == UserAuthType.TABOO_STATUS) return HandUtils.handleResultByCode(HttpStatus.NOT_ACCEPTABLE, null, "机器人账号已被禁言")
        val sendResult = clientChatService.insertChatMessage(type = "text", robotUser.uid, gid, address, content)
        @Suppress("UNCHECKED_CAST")
        if (clientService.getClientData(sendResult as Map<String?, Any>, "code").toInt() == 200) {
            HandUtils.sendGlobalMessage(
                event = "[MESSAGE]",
                content = sendResult["data"],
                server = GlobalService.socketIOServer!!
            )
        }
        return sendResult
    }
}