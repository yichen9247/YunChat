package com.server.handsock.client.controller

import com.server.handsock.client.service.ClientChannelService
import com.server.handsock.common.data.ControllerSearchChannel
import com.server.handsock.common.utils.HandUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/android/channel")
class ClientChannelController @Autowired constructor(private val clientChannelService: ClientChannelService) {
    @GetMapping("/search/all")
    fun searchAllGroup(): Any {
        return clientChannelService.searchAllGroup()
    }

    @PostMapping("/search/gid")
    fun searchGroupByGid(@RequestBody data: ControllerSearchChannel): Any {
        return if (data.gid == null || data.gid < 0) {
            HandUtils.printParamError()
        } else clientChannelService.searchGroupByGid(data.gid)
    }
}
