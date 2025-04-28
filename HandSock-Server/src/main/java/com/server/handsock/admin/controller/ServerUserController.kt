package com.server.handsock.admin.controller

import com.server.handsock.admin.service.ServerUserService
import com.server.handsock.common.data.UserMannerModel
import com.server.handsock.common.utils.HandUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/admin/user")
class ServerUserController @Autowired constructor(private val serverUserService: ServerUserService) {
    @PostMapping("/set/status")
    fun updateUserTabooStatus(@RequestBody data: UserMannerModel): Any {
        return if (data.uid == null || data.uid <= 0 || data.status == null) {
            HandUtils.printParamError()
        } else serverUserService.updateUserStatus(
            uid = data.uid,
            status = data.status
        )
    }

    @PostMapping("/set/info")
    fun updateUserInfo(@RequestBody data: UserMannerModel): Any {
        return if (data.uid == null || data.uid <= 0 || data.robot == null ||  data.username.isNullOrEmpty() || data.avatar.isNullOrEmpty() || data.nick.isNullOrEmpty()) {
            HandUtils.printParamError()
        } else serverUserService.updateUserInfo(
            uid = data.uid,
            nick = data.nick,
            robot = data.robot,
            avatar = data.avatar,
            username = data.username
        )
    }
}
