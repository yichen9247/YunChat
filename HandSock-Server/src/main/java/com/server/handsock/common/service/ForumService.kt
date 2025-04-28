package com.server.handsock.common.service

import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.server.handsock.common.dao.ForumDao
import com.server.handsock.common.model.ForumModel
import com.server.handsock.common.utils.HandUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class ForumService @Autowired constructor(private val forumDao: ForumDao) {
    fun searchForumPost(page: Int?, limit: Int?): Map<String, Any> {
        if (page == null || limit == null || page <= 0 || limit <= 0)
            return HandUtils.printParamError()
        try {
            val pageObj = Page<ForumModel>(page.toLong(), limit.toLong())
            val resultPage = forumDao.selectHierarchicalPosts(pageObj)
            return HandUtils.handleResultByCode(HttpStatus.OK,  mapOf(
                "total" to resultPage.total,
                "items" to resultPage.records
            ), "获取成功")
        } catch (e: Exception) {
            return HandUtils.printErrorLog(e)
        }
    }
}