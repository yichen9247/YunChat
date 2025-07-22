package com.server.yunchat.client.man;

import com.server.yunchat.builder.model.ReportModel;

public class ClientReportManage {
    public void insertRepo(ReportModel reportModel, String sid, Long reporter_id, Long reported_id, String reason) {
        reportModel.setSid(sid);
        reportModel.setReason(reason);
        reportModel.setReporterId(reporter_id);
        reportModel.setReportedId(reported_id);
    }
}
