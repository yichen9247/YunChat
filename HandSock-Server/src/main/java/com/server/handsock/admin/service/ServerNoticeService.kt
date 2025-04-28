package com.server.handsock.admin.service

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.server.handsock.admin.man.ServerNoticeManage
import com.server.handsock.common.dao.NoticeDao
import com.server.handsock.common.model.NoticeModel
import com.server.handsock.common.utils.HandUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class ServerNoticeService @Autowired constructor(private val noticeDao: NoticeDao) {
    fun deleteNotice(nid: Int): Map<String, Any> {
        return if (noticeDao.deleteById(nid) > 0) {
            HandUtils.handleResultByCode(HttpStatus.OK, null, "删除成功")
        } else HandUtils.handleResultByCode(HttpStatus.INTERNAL_SERVER_ERROR, null, "删除失败")
    }

    fun updateNotice(nid: Int, title: String, content: String): Map<String, Any> {
        if (title.length > 20) return HandUtils.handleResultByCode(HttpStatus.NOT_ACCEPTABLE, null, "标题过长")
        if (noticeDao.selectOne(QueryWrapper<NoticeModel>().eq("nid", nid)) == null) return HandUtils.handleResultByCode(HttpStatus.NOT_ACCEPTABLE, null, "公告不存在")
        val noticeModel = NoticeModel()
        ServerNoticeManage().updateNotice(noticeModel, nid, title, content)
        return if (noticeDao.updateById(noticeModel) > 0) {
            HandUtils.handleResultByCode(HttpStatus.OK, null, "修改成功")
        } else HandUtils.handleResultByCode(HttpStatus.INTERNAL_SERVER_ERROR, null, "修改失败")
    }

    fun createNotice(title: String, content: String): Map<String, Any> {
        if (title.length > 20) return HandUtils.handleResultByCode(HttpStatus.NOT_ACCEPTABLE, null, "标题过长")
        val noticeModel = NoticeModel()
        ServerNoticeManage().setNotice(noticeModel, title, content)
        return if (noticeDao.insert(noticeModel) > 0) {
            HandUtils.handleResultByCode(HttpStatus.OK, null, "创建成功")
        } else HandUtils.handleResultByCode(HttpStatus.INTERNAL_SERVER_ERROR, null, "创建失败")
    }
}