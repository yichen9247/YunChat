package com.server.yunchat.socket.handler

import com.server.yunchat.admin.service.ServerSystemService
import com.server.yunchat.builder.data.ResultModel
import com.server.yunchat.builder.data.SocketSystemConfig
import com.server.yunchat.builder.utils.ResultUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ConfigHandler @Autowired constructor(
    private val serverSystemService: ServerSystemService
) {
    fun handleSetSystemConfigValue(data: SocketSystemConfig): ResultModel {
        return if (data.value .isNullOrEmpty() || data.name.isNullOrEmpty())
            ResultUtils.printParamMessage()
        else serverSystemService.setSystemConfigValue(
            name = data.name,
            value = data.value
        )
    }
}
