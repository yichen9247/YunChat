package com.server.yunchat.builder.service

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.server.yunchat.builder.dao.NoticeDao
import com.server.yunchat.builder.data.ResultModel
import com.server.yunchat.builder.model.NoticeModel
import com.server.yunchat.builder.utils.HandUtils
import com.server.yunchat.builder.utils.ResultUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class NoticeObjService @Autowired constructor(private val noticeDao: NoticeDao) {
    fun searchSystemNotice(page: Int?, limit: Int?): ResultModel {
        if (page == null || limit == null || page <= 0 || limit <= 0)
            return ResultUtils.printParamMessage()
        try {
            val pageObj = Page<NoticeModel>(page.toLong(), limit.toLong())
            val wrapper = QueryWrapper<NoticeModel>().orderByDesc("time")
            val queryResult = noticeDao.selectPage(pageObj, wrapper)
            return HandUtils.handleResultByCode(HttpStatus.OK,  mapOf(
                "total" to queryResult.total,
                "items" to queryResult.records
            ), "获取成功")
        } catch (e: Exception) {
            return ResultUtils.printErrorMessage(e)
        }
    }
}