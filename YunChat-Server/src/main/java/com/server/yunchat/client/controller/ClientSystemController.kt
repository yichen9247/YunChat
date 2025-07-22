package com.server.yunchat.client.controller

import com.server.yunchat.admin.service.ServerSystemService
import com.server.yunchat.builder.data.CommonCheckVersion
import com.server.yunchat.builder.utils.ResultUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/android/system")
class AndroidController @Autowired constructor(private val serverSystemService: ServerSystemService) {
    @PostMapping("/update")
    fun checkAppUpdate(@RequestBody data: CommonCheckVersion): Any {
        return if (data.version.isNullOrEmpty()) {
            ResultUtils.printParamMessage()
        } else serverSystemService.checkAppUpdate(data.version)
    }
}