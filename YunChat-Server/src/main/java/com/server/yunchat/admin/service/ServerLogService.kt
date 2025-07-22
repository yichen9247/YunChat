package com.server.yunchat.admin.service

import com.server.yunchat.builder.data.ResultModel
import com.server.yunchat.builder.utils.HandUtils
import com.server.yunchat.builder.utils.ResultUtils
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import java.nio.file.Files
import java.nio.file.Paths

@Service
class ServerLogService {

    fun getSystemLogs(): ResultModel {
        val contentBuilder = StringBuilder()
        try {
            Files.lines(Paths.get(FILE_PATH)).use { lines ->
                lines.forEach { line: String? -> contentBuilder.append(line).append(System.lineSeparator()) }
                return HandUtils.handleResultByCode(HttpStatus.OK, contentBuilder.toString(), "获取成功")
            }
        } catch (e: Exception) {
            return ResultUtils.printErrorMessage(e)
        }
    }

    fun deleteSystemLogs(): ResultModel {
        try {
            Files.write(Paths.get(FILE_PATH), ByteArray(0))
            return HandUtils.handleResultByCode(HttpStatus.OK, null, "日志清空成功")
        } catch (e: Exception) {
            return ResultUtils.printErrorMessage(e)
        }
    }

    companion object {
        private const val FILE_PATH = ".logs/server.log"
    }
}
