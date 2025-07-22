package com.server.yunchat.socket.handler

import com.server.yunchat.builder.data.ResultModel
import com.server.yunchat.builder.data.SocketUserReport
import com.server.yunchat.builder.utils.ResultUtils
import com.server.yunchat.client.service.ClientReportService
import org.springframework.stereotype.Service

@Service
class ReportHandler(private val clientReportService: ClientReportService) {
    fun handleReport(data: SocketUserReport, uid: Long): ResultModel {
        return if (data.sid.isNullOrEmpty() || data.reason.isNullOrEmpty() || data.reportedId == null || data.reason.trim().isEmpty())
            ResultUtils.printParamMessage()
        else clientReportService.addReport(
            sid = data.sid,
            reporterId = uid,
            reason = data.reason,
            reportedId = data.reportedId
        )
    }
}
