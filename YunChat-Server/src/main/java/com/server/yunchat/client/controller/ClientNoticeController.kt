package com.server.yunchat.client.controller

import com.server.yunchat.builder.service.NoticeObjService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/android/notice")
class ClientNoticeController @Autowired constructor(private val noticeObjService: NoticeObjService) {
    @GetMapping("/search/all")
    fun searchAllNotice(): Any {
        return noticeObjService.searchSystemNotice(1, 25)
    }
}