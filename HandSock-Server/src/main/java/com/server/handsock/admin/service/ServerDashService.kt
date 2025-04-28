package com.server.handsock.admin.service

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.core.mapper.BaseMapper
import com.server.handsock.admin.dao.ServerUserDao
import com.server.handsock.common.dao.ChannelDao
import com.server.handsock.common.dao.MessageDao
import com.server.handsock.common.props.HandProp
import com.server.handsock.common.utils.HandUtils
import com.server.handsock.common.utils.SystemUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import oshi.SystemInfo
import java.io.IOException
import java.net.InetAddress
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

@Service
class ServerDashService @Autowired constructor(
    private val handProp: HandProp,
    private val channelDao: ChannelDao,
    private val messageDao: MessageDao,
    private val serverUserDao: ServerUserDao
) {
    private val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    val dashboardData: Map<String, Any>
        get() {
            try {
                val today = LocalDate.now()
                val startOfDay = today.atStartOfDay()
                val endOfDay = today.atTime(LocalTime.MAX)
                val userTotal = serverUserDao.selectCount(null)
                val chanTotal = channelDao.selectCount(null)
                val todayRegUser = countRecordsInRange(serverUserDao, "reg_time", startOfDay, endOfDay)
                val todayChatTotal = countRecordsInRange(messageDao, "time", startOfDay, endOfDay)

                val result: MutableMap<String, Any> = HashMap()
                result["userTotal"] = userTotal
                result["chanTotal"] = chanTotal
                result["todayRegUser"] = todayRegUser
                result["todayChatTotal"] = todayChatTotal
                result["systemOsInfo"] = systemOsInfo
                return HandUtils.handleResultByCode(HttpStatus.OK, result, "获取成功")
            } catch (e: Exception) {
                return HandUtils.printErrorLog(e)
            }
        }

    @get:Throws(IOException::class)
    val systemOsInfo: HashMap<String?, Any?>
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
                    put("appVersion", handProp.appVersion)
                    put("timeZoneId", TimeZone.getDefault().id)
                    put("hostAddress", localHost.hostAddress)
                    put("systemUptime", SystemUtils.systemUptime)
                    put("logicalCount", processor.logicalProcessorCount)
                    put("memoryUsageInfo", SystemUtils.systemMemoryUsage)
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
