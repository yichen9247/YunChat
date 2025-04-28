package com.server.handsock.admin.service

import com.server.handsock.common.utils.HandUtils
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import java.nio.file.Files
import java.nio.file.Paths

@Service
class ServerLogService {
    val systemLogs: Map<String, Any>
        get() {
            val contentBuilder = StringBuilder()
            try {
                Files.lines(Paths.get(FILE_PATH)).use { lines ->
                    lines.forEach { line: String? -> contentBuilder.append(line).append(System.lineSeparator()) }
                    return HandUtils.handleResultByCode(HttpStatus.OK, contentBuilder.toString(), "获取成功")
                }
            } catch (e: Exception) {
                return HandUtils.printErrorLog(e)
            }
        }

    fun deleteSystemLogs(): Map<String, Any> {
        try {
            Files.write(Paths.get(FILE_PATH), ByteArray(0))
            return HandUtils.handleResultByCode(HttpStatus.OK, null, "日志清空成功")
        } catch (e: Exception) {
            return HandUtils.printErrorLog(e)
        }
    }

    companion object {
        private const val FILE_PATH = "server.log"
    }
}
