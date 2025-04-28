package com.server.handsock.admin.service

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.server.handsock.common.dao.CommentDao
import com.server.handsock.common.model.CommentModel
import com.server.handsock.common.utils.HandUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class ServerCommentService @Autowired constructor(private val commentDao: CommentDao) {
     fun getCommentList(page: Int, limit: Int): Map<String, Any> {
        val wrapper = QueryWrapper<CommentModel>()
        val pageObj = Page<CommentModel>(page.toLong(), limit.toLong())
        val queryResult = commentDao.selectPage(pageObj, wrapper)
        return HandUtils.handleResultByCode(HttpStatus.OK,  mapOf(
            "total" to queryResult.total,
            "items" to queryResult.records
        ), "获取成功")
    }

    fun deleteForumComment(cid: Int?): Map<String, Any> {
        val commentModel = commentDao.selectOne(QueryWrapper<CommentModel>().eq("cid", cid))
            ?: return HandUtils.handleResultByCode(HttpStatus.NOT_ACCEPTABLE, null, "评论不存在")
        if (commentDao.deleteById(cid) == 0) return HandUtils.handleResultByCode(HttpStatus.INTERNAL_SERVER_ERROR, null, "删除失败")
        if (commentModel.parent != null) {
            val remainingChildren = commentDao.selectCount(QueryWrapper<CommentModel>().eq("parent", commentModel.parent))
            if (remainingChildren == 0L) commentDao.deleteById(commentModel.parent)
        } else commentDao.delete(QueryWrapper<CommentModel>().eq("parent", cid))
        return HandUtils.handleResultByCode(HttpStatus.OK, null, "删除成功")
    }
}