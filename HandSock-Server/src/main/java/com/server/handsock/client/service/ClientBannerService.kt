package com.server.handsock.client.service

import com.server.handsock.common.dao.BannerDao
import com.server.handsock.common.utils.HandUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class ClientBannerService @Autowired constructor(private val bannerDao: BannerDao) {
    fun searchAllBanner(): Map<String, Any> {
        val bannerList = bannerDao.selectList(null)
        return HandUtils.handleResultByCode(HttpStatus.OK, bannerList, "获取成功")
    }
}