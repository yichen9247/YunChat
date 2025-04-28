package com.server.handsock.client.controller

import com.server.handsock.client.service.ClientForumService
import com.server.handsock.common.data.ControllerAddForumPost
import com.server.handsock.common.service.ForumService
import com.server.handsock.common.utils.HandUtils
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/android/forum")
class ForumController @Autowired constructor(
    private val forumService: ForumService,
    private val clientForumService: ClientForumService
) {
    @GetMapping("/search/all")
    fun searchAllForumPost(): Any {
        return forumService.searchForumPost(1, 25)
    }

    @PostMapping("/add/post")
    fun addForumPost(@RequestBody data: ControllerAddForumPost, request: HttpServletRequest): Any {
        val uid = request.getHeader("uid")?.toLong()
        return if (uid == null || uid <= 0 || data.title.isNullOrEmpty() || data.image.isNullOrEmpty() || data.content.isNullOrEmpty() || data.title.trim().isEmpty() || data.content.trim().isEmpty()) {
            HandUtils.printParamError()
        } else clientForumService.addForumPost(
            uid = uid,
            title = data.title,
            image = data.image,
            content = data.content
        )
    }
}