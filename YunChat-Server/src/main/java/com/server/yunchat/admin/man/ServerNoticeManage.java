package com.server.yunchat.admin.man;

import com.server.yunchat.builder.model.NoticeModel;

public class ServerNoticeManage {
    public void setNotice(NoticeModel noticeModel, String title, String content) {
        noticeModel.setTitle(title);
        noticeModel.setContent(content);
    }

    public void updateNotice(NoticeModel noticeModel, Integer id, String title, String content) {
        noticeModel.setId(id);
        setNotice(noticeModel, title, content);
    }
}
