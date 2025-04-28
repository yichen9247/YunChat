package com.server.handsock.common.service

import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.server.handsock.common.dao.CommentDao
import com.server.handsock.common.model.CommentModel
import com.server.handsock.common.utils.HandUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class CommentService @Autowired constructor(private val commentDao: CommentDao) {
    fun searchPostComment(pid: Int?, page: Int?, limit: Int?): Map<String, Any> {
        if (pid == null || page == null || limit == null || pid <= 0 || page <= 0 || limit <= 0)
            return HandUtils.printParamError()
        try {
            val pageObj = Page<CommentModel>(page.toLong(), limit.toLong())
            val resultPage = commentDao.selectHierarchicalComments(pageObj, pid)
            return HandUtils.handleResultByCode(HttpStatus.OK, mapOf(
                "total" to resultPage.total,
                "items" to resultPage.records
            ), "获取成功")
        } catch (e: Exception) {
            return HandUtils.printErrorLog(e)
        }
    }
}