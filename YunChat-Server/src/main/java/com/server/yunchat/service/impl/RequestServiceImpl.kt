package com.server.yunchat.service.impl

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.Gson
import com.server.yunchat.builder.data.ControllerUserBind
import com.server.yunchat.builder.data.TencentValidModel
import com.server.yunchat.builder.utils.ConsoleUtils
import com.server.yunchat.service.RequestService
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

/**
 * @name 网络服务实现类
 * @author yichen9247
 */
@Service
class RequestServiceImpl: RequestService {
    /**
     * @name 获取随机一言
     * @return String
     */
    override fun getHitokoto(): String = safeFetch(HITOKOTO_URL) {
        it["hitokoto"] as? String ?: "ERROR"
    } ?: FETCH_FAILED

    /**
     * @name 获取微博热搜
     * @return String
     */
    override fun getWeiboHotSearch(): String = safeFetch(WEIBO_HOT_SEARCH_URL) { responseMap ->
        responseMap.parseList("data", "realtime", contentKey = "word")
    } ?: FETCH_FAILED

    /**
     * @name 获取破站热搜
     * @return String
     */
    override fun getBilibiliHotSearch(): String = safeFetch(BILIBILI_HOT_SEARCH_URL) { responseMap ->
        responseMap.parseList("data", "trending", "list", contentKey = "keyword")
    } ?: FETCH_FAILED

    /**
     * @name 获取地址归属
     * @return String
     */
    override fun getAttributionByIp(ip: String): String {
        return try {
            val url = IP_ATTRIBUTION_URL.format(ip)
            val result = restTemplate.getForObject(url, String::class.java)
            val responseMap = objectMapper.readValue(result, Map::class.java) as? Map<*, *>
            @Suppress("UNCHECKED_CAST")
            (responseMap?.get("data") as? List<Map<String, String>>)
                ?.firstOrNull()
                ?.get("location")
                ?: "未知"
        } catch (e: Exception) {
            ConsoleUtils.printErrorLog(e)
            "未知"
        }
    }

    /**
     * @name 验证QQ登录
     * @param data 数据
     */
    override fun validTencentLogin(data: ControllerUserBind): Boolean {
        return try {
            val url = VALID_TENCENT_LOGIN_URL.format(data.access_token)
            val result = restTemplate.getForObject(url, String::class.java)
            val resultModel = Gson().fromJson(result
                ?.removePrefix("callback(")
                ?.removeSuffix(");")
                ?.trim(), TencentValidModel::class.java)
            return if (resultModel.error == null) {
                resultModel.openid == data.openid
            } else false
        } catch (e: Exception) {
            ConsoleUtils.printErrorLog(e)
            false
        }
    }

    /**
     * @name 深度获取数据
     * @param keys 键
     */
    private fun Map<*, *>.deepGet(vararg keys: String): Any? {
        return keys.fold(this as Any?) { acc, key ->
            if (acc is Map<*, *>) acc[key] else null
        }
    }

    /**
     * @name 安全获取数据
     * @param url 请求地址
     * @param processor 处理器
     */
    private fun safeFetch(url: String, processor: (Map<*, *>) -> String?): String? {
        return try {
            val result = restTemplate.getForObject(url, String::class.java)
            objectMapper.readValue(result, Map::class.java)?.let(processor)
        } catch (e: Exception) {
            ConsoleUtils.printErrorLog(e)
            null
        }
    }

    /**
     * @name 解析数据列表
     * @param keys 键
     * @param contentKey 内容键
     */
    private fun Map<*, *>.parseList(vararg keys: String, contentKey: String): String? = this.deepGet(*keys)?.let { list ->
        @Suppress("UNCHECKED_CAST")
        (list as? List<Map<String, Any>>)
            ?.take(10)
            ?.mapIndexed { index, item -> "${index + 1}、${item[contentKey]}" }
            ?.joinToString("<br/>")
    }

    companion object {
        private val restTemplate = RestTemplate()
        private val objectMapper = ObjectMapper()
        private const val FETCH_FAILED = "获取失败，请查看系统日志"
        private const val HITOKOTO_URL = "https://international.v1.hitokoto.cn"
        private const val WEIBO_HOT_SEARCH_URL = "https://weibo.com/ajax/side/hotSearch"
        private const val VALID_TENCENT_LOGIN_URL = "https://graph.qq.com/oauth2.0/me?access_token=%s"
        private const val BILIBILI_HOT_SEARCH_URL = "https://api.bilibili.com/x/web-interface/wbi/search/square?limit=10"
        private const val IP_ATTRIBUTION_URL = "https://opendata.baidu.com/api.php?query=%s&co=&resource_id=6006&oe=utf8"
    }
}