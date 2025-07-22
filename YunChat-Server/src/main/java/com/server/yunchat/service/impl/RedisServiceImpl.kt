package com.server.yunchat.service.impl

import com.server.yunchat.service.RedisService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

/**
 * @name 缓存服务实现类
 * @author yichen9247
 */
@Service
class RedisServiceImpl @Autowired constructor(
    @Qualifier("defaultRedisTemplate")
    private val defaultRedisTemplate: RedisTemplate<String, String>
): RedisService {

    companion object {
        private const val TAG_NAME = "handsock"
    }

    /**
     * @name 删除扫码状态
     * @param qid 扫码ID
     */
    override fun delUserScanStatus(qid: String) {
        val scanStatus = defaultRedisTemplate.opsForValue()["$TAG_NAME-scanStatus:$qid"]
        if (scanStatus != null) defaultRedisTemplate.delete("$TAG_NAME-scanStatus:$qid")
    }

    /**
     * @name 获取扫码状态
     * @param qid 扫码ID
     */
    override fun getUserScanStatus(qid: String): String? {
        return defaultRedisTemplate.opsForValue()["$TAG_NAME-scanStatus:$qid"]
    }

    /**
     * @name 获取扫码结果
     * @param qid 扫码ID
     */
    override fun getUserScanResult(qid: String): String? {
        return defaultRedisTemplate.opsForValue()["$TAG_NAME-scanTargetUser:$qid"]
    }

    /**
     * @name 设置扫码结果
     * @param qid 扫码ID
     * @param uid 用户ID
     */
    override fun setUserScanResult(qid: String, uid: String) {
        defaultRedisTemplate.opsForValue()["$TAG_NAME-scanTargetUser:$qid", uid, 45] = TimeUnit.SECONDS
    }

    /**
     * @name 设置扫码状态
     * @param qid 扫码ID
     * @param status 扫码状态
     */
    override fun setUserScanStatus(qid: String, status: Int) {
        defaultRedisTemplate.opsForValue()["$TAG_NAME-scanStatus:$qid", status.toString(), 30] = TimeUnit.SECONDS
    }

    /**
     * @name 设置接口缓存
     * @param address IP地址
     */
    override fun setOpenApiCache(address: String) {
        defaultRedisTemplate.opsForValue()["$TAG_NAME-apiCache:$address", "ok", 3] = TimeUnit.SECONDS
    }

    /**
     * @name 获取接口缓存
     * @param address IP地址
     */
    override fun getOpenApiCache(address: String): Boolean {
        val requestStatus = defaultRedisTemplate.opsForValue()["$TAG_NAME-apiCache:$address"]
        return requestStatus == null
    }

    /**
     * @name 验证消息缓存
     * @param uid 用户ID
     */
    override fun validUserMessageCache(uid: Long): Boolean {
        val cache = defaultRedisTemplate.opsForValue()["$TAG_NAME-msgCache:$uid"]
        if (cache == null) setUserMessageCache(uid)
        return cache == null
    }

    /**
     * @name 设置消息缓存
     * @param uid 用户ID
     */
    private fun setUserMessageCache(uid: Long) {
        defaultRedisTemplate.opsForValue()["$TAG_NAME-msgCache:$uid", "ok", 2] = TimeUnit.SECONDS
    }
}