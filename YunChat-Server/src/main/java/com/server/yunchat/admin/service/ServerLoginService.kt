package com.server.yunchat.admin.service

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.server.yunchat.builder.dao.LoginDao
import com.server.yunchat.builder.data.ResultModel
import com.server.yunchat.builder.model.LoginModel
import com.server.yunchat.builder.utils.HandUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class ServerLoginService @Autowired constructor(
    private val loginDao: LoginDao
) {
    fun getLoginList(page: Int, limit: Int): ResultModel {
        val wrapper = QueryWrapper<LoginModel>().orderByDesc("time")
        val pageObj = Page<LoginModel>(page.toLong(), limit.toLong())
        val queryResult = loginDao.selectPage(pageObj, wrapper)
        return HandUtils.handleResultByCode(
            HttpStatus.OK,  mapOf(
            "total" to queryResult.total,
            "items" to queryResult.records
        ), "获取成功")
    }
}