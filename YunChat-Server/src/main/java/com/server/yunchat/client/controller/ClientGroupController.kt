package com.server.yunchat.client.controller

import com.server.yunchat.builder.data.ResultModel
import com.server.yunchat.builder.utils.ResultUtils
import com.server.yunchat.client.mod.GroupOperateModel
import com.server.yunchat.client.service.ClientGroupService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/android/group")
class ClientGroupController @Autowired constructor(private val clientGroupService: ClientGroupService) {
    @GetMapping("/list")
    fun getGroupList(
        @RequestAttribute("USER_ID") uid: Long?
    ): ResultModel {
        return if (uid == null || uid <= 0) {
            ResultUtils.printParamMessage()
        } else clientGroupService.getGroupList(uid)
    }

    @PostMapping("/join")
    fun joinGroup(
        @RequestBody data: GroupOperateModel,
        @RequestAttribute("USER_ID") uid: Long?
    ): ResultModel {
        return if (uid == null || uid <= 0) {
            ResultUtils.printParamMessage()
        } else clientGroupService.addGroupMember(data.gid, uid)
    }

    @PostMapping("/exit")
    fun exitGroup(
        @RequestBody data: GroupOperateModel,
        @RequestAttribute("USER_ID") uid: Long?
    ): ResultModel {
        return if (uid == null || uid <= 0) {
            ResultUtils.printParamMessage()
        } else clientGroupService.deleteGroupMember(data.gid, uid)
    }

    @GetMapping("search")
    fun searchGroupList(
        @RequestParam("value") value: String?
    ): ResultModel {
        return if (value.isNullOrEmpty()) {
            ResultUtils.printParamMessage()
        } else clientGroupService.searchGroupList(value)
    }
}
