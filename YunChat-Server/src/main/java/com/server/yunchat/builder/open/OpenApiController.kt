package com.server.yunchat.builder.open

import com.server.yunchat.builder.data.ResultModel
import com.server.yunchat.builder.open.model.SendRobotMessageApiModel
import com.server.yunchat.service.impl.RobotServiceImpl
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/openapi")
open class OpenAPIController @Autowired constructor(
    private val robotServiceImpl: RobotServiceImpl
) {
    @PostMapping("/robot/send")
    fun sendRobotMessage(
        @Valid @RequestBody data: SendRobotMessageApiModel,
    ): ResultModel {
        return robotServiceImpl.sendRobotMessage(
            tar = data.tar,
            obj = data.obj,
            type = data.type,
            content = data.content
        )
    }
}