package com.server.handsock.client.service

import com.server.handsock.client.man.ClientForumManage
import com.server.handsock.common.dao.ForumDao
import com.server.handsock.common.model.ForumModel
import com.server.handsock.common.utils.HandUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class ClientForumService @Autowired constructor(
    private val forumDao: ForumDao
) {
    fun addForumPost(uid: Long, title: String, content: String, image: String): Map<String, Any> {
        if (content.length > 1000 || title.length > 15) return HandUtils.handleResultByCode(HttpStatus.NOT_ACCEPTABLE, null, "内容或标题过长")
        val forumModel = ForumModel()
        ClientForumManage().insertForumPost(forumModel, uid, title, content, image)
        return if (forumDao.insert(forumModel) > 0) {
            HandUtils.handleResultByCode(HttpStatus.OK, null, "发布成功")
        } else HandUtils.handleResultByCode(HttpStatus.INTERNAL_SERVER_ERROR, null, "发布失败")
    }
}