package com.server.yunchat.service

interface RedisService {
    fun delUserScanStatus(qid: String)
    fun getUserScanStatus(qid: String): String?
    fun getUserScanResult(qid: String): String?
    fun setUserScanResult(qid: String, uid: String)
    fun setUserScanStatus(qid: String, status: Int)

    fun setOpenApiCache(address: String)
    fun getOpenApiCache(address: String): Boolean
    fun validUserMessageCache(uid: Long): Boolean
}