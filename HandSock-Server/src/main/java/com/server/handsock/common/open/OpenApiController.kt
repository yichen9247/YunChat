package com.server.handsock.common.open

import com.server.handsock.client.service.ClientChatService
import com.server.handsock.client.service.ClientUserService
import com.server.handsock.common.utils.HandUtils
import com.server.handsock.common.utils.RobotUtils
import com.server.handsock.service.ClientService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/openapi")
open class OpenAPIController @Autowired constructor(
    private val clientService: ClientService,
    private val clientUserService: ClientUserService,
    private val clientChatService: ClientChatService
) {
    @PostMapping("/robot/send")
    fun sendRobotMessage(@RequestBody data: Map<String?, Any>, request: HttpServletRequest): Any {
        return if (data["gid"] == null || data["content"] == null) {
            HandUtils.handleResultByCode(HttpStatus.NOT_ACCEPTABLE, null, "必填参数不为空")
        } else {
            val gid = data["gid"].toString()
            val content = data["content"].toString()
            RobotUtils.sendRobotMessage(
                content = content,
                gid = gid.toLong(),
                address = request.remoteAddr,
                clientService = clientService,
                clientUserService = clientUserService,
                clientChatService = clientChatService
            )
        }
    }
}