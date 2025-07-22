package com.server.yunchat.service.impl

import com.corundumstudio.socketio.SocketIOClient
import com.server.yunchat.service.ClientService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Service
import java.net.InetSocketAddress

/**
 * @name 客户服务实现类
 * @author yichen9247
 */
@Service
class ClientServiceImpl: ClientService {
    /**
     * @name 获取HTTP客户端IP
     * @param request HTTP请求
     */
    override fun getHttpClientIp(request: HttpServletRequest): String {
        var ip: String? = request.getHeader("X-Forwarded-For")
        if (ip.isNullOrBlank() || "unknown".equals(ip, ignoreCase = true)) {
            ip = request.getHeader("Proxy-Client-IP")
        }
        if (ip.isNullOrBlank() || "unknown".equals(ip, ignoreCase = true)) {
            ip = request.getHeader("WL-Proxy-Client-IP")
        }
        if (ip.isNullOrBlank() || "unknown".equals(ip, ignoreCase = true)) {
            ip = request.getHeader("HTTP_CLIENT_IP")
        }
        if (ip.isNullOrBlank() || "unknown".equals(ip, ignoreCase = true)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR")
        }
        if (ip.isNullOrBlank() || "unknown".equals(ip, ignoreCase = true)) {
            ip = request.remoteAddr
        }
        return ip?.split(",")?.firstOrNull()?.trim() ?: "unknown"
    }

    /**
     * @name 获取Socket客户端IP
     * @param client Socket客户端
     */
    override fun getSocketClientIp(client: SocketIOClient): String {
        val remoteAddr = client.remoteAddress
        if (remoteAddr is InetSocketAddress) {
            return remoteAddr.address?.hostAddress ?: remoteAddr.hostString
        }
        val addrStr = remoteAddr.toString().substring(1)
        return if (addrStr.startsWith("[")) { // IPv6
            val endIndex = addrStr.indexOf(']')
            if (endIndex != -1) addrStr.substring(1, endIndex) else addrStr
        } else addrStr.substringBeforeLast(':') // IPv4
    }

    /**
     * @name 获取Socket客户端版本
     * @param client Socket客户端
     */
    override fun getSocketClientVersion(client: SocketIOClient): String {
        @Suppress("UNCHECKED_CAST")
        val authToken = client.handshakeData.authToken as Map<String, Any>
        return authToken["version"]?.toString() ?: ""
    }
}