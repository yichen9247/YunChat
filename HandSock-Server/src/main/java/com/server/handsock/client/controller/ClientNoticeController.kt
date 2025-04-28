package com.server.handsock.client.controller

import com.server.handsock.common.service.NoticeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/android/notice")
class ClientNoticeController @Autowired constructor(private val noticeService: NoticeService) {
    @GetMapping("/search/all")
    fun searchAllNotice(): Any {
        return noticeService.searchSystemNotice(1, 25)
    }
}