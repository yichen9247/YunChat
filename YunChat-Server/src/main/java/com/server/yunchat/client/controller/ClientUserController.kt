package com.server.yunchat.client.controller

import com.server.yunchat.builder.data.ControllerUserBind
import com.server.yunchat.builder.data.ResultModel
import com.server.yunchat.builder.data.UserDecryptLoginModel
import com.server.yunchat.builder.utils.HandUtils
import com.server.yunchat.builder.utils.ResultUtils
import com.server.yunchat.client.mod.ScanLoginModel
import com.server.yunchat.client.mod.UserAuthApiModel
import com.server.yunchat.client.mod.UserUpdateModel
import com.server.yunchat.client.service.ClientUserService
import com.server.yunchat.service.impl.ClientServiceImpl
import com.server.yunchat.service.impl.EncryptServiceImpl
import com.server.yunchat.service.impl.RedisServiceImpl
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/android/user")
class ClientUserController @Autowired constructor(
    private val redisServiceImpl: RedisServiceImpl,
    private val clientUserService: ClientUserService,
    private val clientServiceImpl: ClientServiceImpl,
    private val encryptServiceImpl: EncryptServiceImpl
) {
    @PostMapping("/login")
    fun userLogin(
        request: HttpServletRequest,
        @Valid @RequestBody data: UserAuthApiModel
    ): ResultModel {
        val loginData = validateRequest(data) ?: return ResultUtils.printForbiddenMessage()
        return clientUserService.loginUser(
            platform = loginData.platform,
            username = loginData.username,
            password = loginData.password,
            address = clientServiceImpl.getHttpClientIp(request)
        )
    }

    @PostMapping("/register")
    fun userRegister(
        request: HttpServletRequest,
        @Valid @RequestBody data: UserAuthApiModel
    ): ResultModel {
        val loginData = validateRequest(data) ?: return ResultUtils.printForbiddenMessage()
        return clientUserService.registerUser(
            platform = loginData.platform,
            username = loginData.username,
            password = loginData.password,
            address = clientServiceImpl.getHttpClientIp(request)
        )
    }

    @PostMapping("/login/qq")
    fun userQQLogin(
        request: HttpServletRequest,
        @RequestBody data: ControllerUserBind,
    ): ResultModel {
        return if (data.openid.isNullOrEmpty() || data.access_token.isNullOrEmpty()) {
            ResultUtils.printParamMessage()
        } else clientUserService.loginUserWithQQ(
            data = data,
            address = clientServiceImpl.getHttpClientIp(request),
        )
    }

    @GetMapping("/info")
    fun getUserInfo(@RequestParam("uid") uid: Long?): ResultModel {
        return if (uid == null || uid <= 0) {
            ResultUtils.printParamMessage()
        } else clientUserService.getUserInfo(uid)
    }

    @GetMapping("/bind")
    fun getUserBind(@RequestAttribute("USER_ID") uid: Long): ResultModel {
        return clientUserService.getUserBind(uid)
    }

    @PostMapping("/bind/qq")
    fun setUserQQBind(
        @RequestParam("status") status: Int?,
        @RequestBody data: ControllerUserBind,
        @RequestAttribute("USER_ID") uid: Long
    ): ResultModel {
        return if (status == null || data.openid.isNullOrEmpty() || data.access_token.isNullOrEmpty()) {
            ResultUtils.printParamMessage()
        } else if (status == 0) clientUserService.unBindUserTencentId(uid) else clientUserService.bindUserTencentId(uid, data)
    }

    @PostMapping("/edit/avatar")
    fun editAvatar(
        @RequestBody data: UserUpdateModel,
        @RequestAttribute("USER_ID") uid: Long
    ): Any {
        return if (data.data.isNullOrEmpty()) {
            ResultUtils.printParamMessage()
        } else clientUserService.updateUserAvatar(uid, data.data)
    }

    @PostMapping("/edit/nick")
    fun editNick(
        @RequestBody data: UserUpdateModel,
        @RequestAttribute("USER_ID") uid: Long
    ): Any {
        return if (data.data.isNullOrEmpty()) {
            ResultUtils.printParamMessage()
        } else clientUserService.updateUserNick(uid, data.data)
    }

    @PostMapping("/edit/password")
    fun editPassword(
        @RequestBody data: UserUpdateModel,
        @RequestAttribute("USER_ID") uid: Long
    ): Any {
        return if (data.data.isNullOrEmpty()) {
            ResultUtils.printParamMessage()
        } else clientUserService.updateUserPassword(uid, data.data)
    }

    @PostMapping("/login/scan")
    fun userScanLogin(
        @RequestBody data: ScanLoginModel,
        @RequestAttribute("USER_ID") uid: Long
    ): ResultModel {
        return when(data.status) {
            0 -> {
                redisServiceImpl.delUserScanStatus(data.qid)
                HandUtils.handleResultByCode(HttpStatus.OK, null, "已取消登录")
            }
            1 -> {
                redisServiceImpl.setUserScanStatus(data.qid, 1)
                redisServiceImpl.setUserScanResult(
                    qid = data.qid,
                    uid = uid.toString()
                )
                HandUtils.handleResultByCode(HttpStatus.OK, null, "登录成功")
            }
            else -> HandUtils.handleResultByCode(HttpStatus.NOT_ACCEPTABLE, null, "未知参数")
        }
    }

    @PostMapping("/login/scan/status")
    fun getQrcodeScanStatus(@RequestBody data: ScanLoginModel, request: HttpServletRequest): ResultModel {
        val address = clientServiceImpl.getHttpClientIp(request)
        return clientUserService.getUserQrcodeScanStatus(data.qid, address)
    }

    private fun validateRequest(data: UserAuthApiModel): UserDecryptLoginModel? {
        val encryptedData = data.data
        if (encryptedData.isNullOrEmpty()) return null
        return encryptServiceImpl.decryptLogin(encryptedData)
    }
}