package com.server.handsock.client.controller

import com.server.handsock.client.service.ClientBannerService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/android/banner")
class ClientBannerController @Autowired constructor(private val clientBannerService: ClientBannerService) {
    @GetMapping("/search/all")
    fun searchAllBanner(): Any {
        return clientBannerService.searchAllBanner()
    }
}