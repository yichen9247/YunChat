package com.server.yunchat.admin.service

import com.server.yunchat.builder.dao.NoticeDao
import com.server.yunchat.builder.data.ResultModel
import com.server.yunchat.builder.model.NoticeModel
import com.server.yunchat.builder.utils.HandUtils
import com.server.yunchat.service.impl.NoticeServiceImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class ServerNoticeService @Autowired constructor(
    private val noticeDao: NoticeDao,
    private val noticeServiceImpl: NoticeServiceImpl
) {
    fun deleteNotice(id: Int): ResultModel {
        return if (noticeDao.deleteById(id) > 0) {
            HandUtils.handleResultByCode(HttpStatus.OK, null, "删除成功")
        } else HandUtils.handleResultByCode(HttpStatus.INTERNAL_SERVER_ERROR, null, "删除失败")
    }

    fun updateNotice(id: Int, title: String, content: String): ResultModel {
        return try {
            val noticeModel = NoticeModel()
            noticeModel.id = id
            val result = noticeServiceImpl.setNotice(title, content, noticeModel)
            if (noticeDao.updateById(result) > 0) {
                HandUtils.handleResultByCode(HttpStatus.OK, null, "修改成功")
            } else HandUtils.handleResultByCode(HttpStatus.INTERNAL_SERVER_ERROR, null, "修改失败")
        } catch (e: Exception) {
            HandUtils.handleResultByCode(HttpStatus.INTERNAL_SERVER_ERROR, null, e.message.toString())
        }
    }

    fun createNotice(title: String, content: String): ResultModel {
        return try {
            val noticeModel = NoticeModel()
            val result = noticeServiceImpl.setNotice(title, content, noticeModel)
            if (noticeDao.insert(result) > 0) {
                HandUtils.handleResultByCode(HttpStatus.OK, null, "创建成功")
            } else HandUtils.handleResultByCode(HttpStatus.INTERNAL_SERVER_ERROR, null, "创建失败")
        } catch (e: Exception) {
            HandUtils.handleResultByCode(HttpStatus.INTERNAL_SERVER_ERROR, null, e.message.toString())
        }
    }
}