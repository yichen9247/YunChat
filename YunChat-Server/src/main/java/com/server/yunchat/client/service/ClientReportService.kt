package com.server.yunchat.client.service

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.server.yunchat.builder.dao.MessageDao
import com.server.yunchat.builder.dao.ReportDao
import com.server.yunchat.builder.data.ResultModel
import com.server.yunchat.builder.model.MessageModel
import com.server.yunchat.builder.model.ReportModel
import com.server.yunchat.builder.utils.HandUtils
import com.server.yunchat.client.dao.ClientUserDao
import com.server.yunchat.client.man.ClientReportManage
import com.server.yunchat.client.mod.ClientUserModel
import lombok.Getter
import lombok.Setter
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service @Setter @Getter
class ClientReportService(
    private val reportDao: ReportDao,
    private val messageDao: MessageDao,
    private val clientUserDao: ClientUserDao
) {
    fun addReport(sid: String, reporterId: Long, reportedId: Long, reason: String): ResultModel {
        if (reason.length > 50) return HandUtils.handleResultByCode(HttpStatus.NOT_ACCEPTABLE, null, "理由过长")
        val reportModel = ReportModel()
        messageDao.selectOne(QueryWrapper<MessageModel>().eq("sid", sid))
            ?: return HandUtils.handleResultByCode(HttpStatus.NOT_ACCEPTABLE, null, "未查找到消息")
        clientUserDao.selectOne(QueryWrapper<ClientUserModel>().eq("uid", reportedId))
            ?: return HandUtils.handleResultByCode(HttpStatus.NOT_ACCEPTABLE, null, "被举报者不存在")
        if (reporterId == reportedId) return HandUtils.handleResultByCode(HttpStatus.NOT_ACCEPTABLE, null, "不能举报自己")
        ClientReportManage().insertRepo(reportModel, sid, reporterId, reportedId, reason)
        return if (reportDao.insert(reportModel) > 0) {
            HandUtils.handleResultByCode(HttpStatus.OK, null, "举报成功")
        } else HandUtils.handleResultByCode(HttpStatus.INTERNAL_SERVER_ERROR, null, "举报失败")
    }
}
