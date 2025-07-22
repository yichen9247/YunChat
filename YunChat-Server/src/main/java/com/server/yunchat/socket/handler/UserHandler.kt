package com.server.yunchat.socket.handler

import com.server.yunchat.builder.data.ResultModel
import com.server.yunchat.builder.data.UserUpdateAvatar
import com.server.yunchat.builder.data.UserUpdateNick
import com.server.yunchat.builder.data.UserUpdatePassword
import com.server.yunchat.builder.utils.ResultUtils
import com.server.yunchat.client.service.ClientUserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class UserHandler @Autowired constructor(private val clientUserService: ClientUserService) {
    fun handleUpdateUserNick(uid: Long, data: UserUpdateNick): ResultModel {
        return if (data.nick.isNullOrEmpty())
            ResultUtils.printParamMessage()
        else clientUserService.updateUserNick(uid, data.nick)
    }

    fun handleUpdateUserAvatar(uid: Long, data: UserUpdateAvatar): ResultModel {
        return if (data.path.isNullOrEmpty())
            ResultUtils.printParamMessage()
        else clientUserService.updateUserAvatar(uid, data.path)
    }

    fun handleUpdateUserPassword(uid: Long, data: UserUpdatePassword): ResultModel {
        return if (data.password.isNullOrEmpty())
            ResultUtils.printParamMessage()
        else clientUserService.updateUserPassword(uid, data.password)
    }
}
