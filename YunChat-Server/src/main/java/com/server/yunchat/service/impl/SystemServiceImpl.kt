package com.server.yunchat.service.impl

import com.server.yunchat.builder.utils.HandUtils
import com.server.yunchat.service.SystemService
import org.springframework.stereotype.Service
import oshi.SystemInfo
import java.lang.management.ManagementFactory

/**
 * @name 系统服务实现类
 * @author yichen9247
 */
@Service
class SystemServiceImpl: SystemService {
    /**
     * @name 获取系统运行时间
     * @return String
     */
    override fun getSystemUptime(): String {
        val uptime = ManagementFactory.getRuntimeMXBean().uptime
        var seconds = uptime / 1000
        val days = seconds / 86400
        seconds %= 86400
        val hours = seconds / 3600
        seconds %= 3600
        val minutes = seconds / 60
        seconds %= 60
        return String.format("%d天%d小时%d分钟%d秒", days, hours, minutes, seconds)
    }

    /**
     * @name 获取系统运行内存
     * @return String
     */
    override fun getSystemMemory(): String {
        val memory = SystemInfo().hardware.memory
        val totalMemory = memory.total // 总内存
        val availableMemory = memory.available // 可用内存
        val usedMemory = totalMemory - availableMemory // 已使用内存
        return HandUtils.formatBytesForString(usedMemory) + "/" + HandUtils.formatBytesForString(totalMemory)
    }
}