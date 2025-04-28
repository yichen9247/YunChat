package com.server.handsock.common.service

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.server.handsock.common.dao.NoticeDao
import com.server.handsock.common.model.NoticeModel
import com.server.handsock.common.utils.HandUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class NoticeService @Autowired constructor(private val noticeDao: NoticeDao) {
    fun searchSystemNotice(page: Int?, limit: Int?): Map<String, Any> {
        if (page == null || limit == null || page <= 0 || limit <= 0)
            return HandUtils.printParamError()
        try {
            val pageObj = Page<NoticeModel>(page.toLong(), limit.toLong())
            val wrapper = QueryWrapper<NoticeModel>().orderByDesc("time")
            val queryResult = noticeDao.selectPage(pageObj, wrapper)
            return HandUtils.handleResultByCode(HttpStatus.OK,  mapOf(
                "total" to queryResult.total,
                "items" to queryResult.records
            ), "获取成功")
        } catch (e: Exception) {
            return HandUtils.printErrorLog(e)
        }
    }
}