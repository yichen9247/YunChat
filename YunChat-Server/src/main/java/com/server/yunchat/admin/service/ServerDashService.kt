package com.server.yunchat.admin.service

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.core.mapper.BaseMapper
import com.server.yunchat.admin.dao.ServerUserDao
import com.server.yunchat.builder.dao.GroupDao
import com.server.yunchat.builder.dao.MessageDao
import com.server.yunchat.builder.data.ResultModel
import com.server.yunchat.builder.props.YunChatProps
import com.server.yunchat.builder.utils.HandUtils
import com.server.yunchat.service.impl.SystemServiceImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import oshi.SystemInfo
import java.net.InetAddress
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

@Service
class ServerDashService @Autowired constructor(
    private val groupDao: GroupDao,
    private val messageDao: MessageDao,
    private val serverUserDao: ServerUserDao,
    private val yunChatProps: YunChatProps,
    private val systemServiceImpl: SystemServiceImpl
) {
    private val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    fun getDashboardData(): ResultModel {
        val today = LocalDate.now()
        val startOfDay = today.atStartOfDay()
        val endOfDay = today.atTime(LocalTime.MAX)
        val userTotal = serverUserDao.selectCount(null)
        val chanTotal = groupDao.selectCount(null)
        val todayRegUser = countRecordsInRange(serverUserDao, "reg_time", startOfDay, endOfDay)
        val todayChatTotal = countRecordsInRange(messageDao, "time", startOfDay, endOfDay)

        val result: MutableMap<String, Any> = HashMap()
        result["userTotal"] = userTotal
        result["groupTotal"] = chanTotal
        result["todayRegUser"] = todayRegUser
        result["systemOsInfo"] = systemOsInfo
        result["todayChatTotal"] = todayChatTotal
        return HandUtils.handleResultByCode(HttpStatus.OK, result, "获取成功")
    }

    private val systemOsInfo: HashMap<String?, Any?>
        get() {
            val si = SystemInfo()
            val hal = si.hardware
            val processor = hal.processor
            val localHost = InetAddress.getLocalHost()

            return object : HashMap<String?, Any?>() {
                init {
                    put("osInfo", System.getProperty("os.name"))
                    put("osArch", System.getProperty("os.arch"))
                    put("locale", LocaleContextHolder.getLocale())
                    put("hostName", localHost.hostName)
                    put("appVersion", yunChatProps.appVersion)
                    put("timeZoneId", TimeZone.getDefault().id)
                    put("hostAddress", localHost.hostAddress)
                    put("logicalCount", processor.logicalProcessorCount)
                    put("systemUptime", systemServiceImpl.getSystemUptime())
                    put("memoryUsageInfo", systemServiceImpl.getSystemMemory())
                }
            }
        }

    private fun <T> countRecordsInRange(dao: BaseMapper<T>, timeField: String, start: LocalDateTime, end: LocalDateTime): Long {
        val queryWrapper = QueryWrapper<T>()
        queryWrapper.ge(timeField, start.format(dateFormatter))
        queryWrapper.lt(timeField, end.format(dateFormatter))
        return dao.selectCount(queryWrapper)
    }
}
