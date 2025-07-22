package com.server.yunchat.builder.service

import com.server.yunchat.builder.dao.LoginDao
import com.server.yunchat.builder.model.LoginModel
import com.server.yunchat.client.man.ClientLoginManage
import com.server.yunchat.service.impl.RequestServiceImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

@Service
open class AsyncService @Autowired constructor(
    private val loginDao: LoginDao,
    private val requestServiceImpl: RequestServiceImpl
) {
    /**
     * 异步获取IP归属地并插入日志
     */
    @Async
    open fun insertUserLoginLog(uid: Long, username: String, address: String, platform: String) {
        val loginModel = LoginModel()
        val attribution = requestServiceImpl.getAttributionByIp(address)
        ClientLoginManage().insertClientLoginLog(loginModel, uid, address, attribution, platform, username)
        loginDao.insert(loginModel)
    }
}