package com.server.handsock.admin.service

import com.server.handsock.common.dao.ForumDao
import com.server.handsock.common.utils.HandUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class ServerForumService @Autowired constructor(
    private val forumDao: ForumDao
) {
    fun deleteForumPost(pid: Int): Map<String, Any> {
        return if (forumDao.deleteById(pid) > 0) {
            HandUtils.handleResultByCode(HttpStatus.OK, null, "删除成功")
        } else HandUtils.handleResultByCode(HttpStatus.INTERNAL_SERVER_ERROR, null, "删除失败")
    }
}