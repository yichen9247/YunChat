package com.server.yunchat.admin.controller

import com.server.yunchat.admin.service.ServerDashService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/admin/dash")
class ServerDashController @Autowired constructor(private val serverDashService: ServerDashService) {
    @GetMapping("/data")
    fun getDashboardData(): Any {
        return serverDashService.getDashboardData()
    }
}
