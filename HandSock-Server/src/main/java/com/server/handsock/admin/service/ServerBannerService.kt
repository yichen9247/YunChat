package com.server.handsock.admin.service

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.server.handsock.admin.man.ServerBannerManage
import com.server.handsock.common.dao.BannerDao
import com.server.handsock.common.model.BannerModel
import com.server.handsock.common.utils.HandUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class ServerBannerService @Autowired constructor(private val bannerDao: BannerDao) {
    fun getBannerList(page: Int, limit: Int): Map<String, Any> {
        val wrapper = QueryWrapper<BannerModel>()
        val pageObj = Page<BannerModel>(page.toLong(), limit.toLong())
        val queryResult = bannerDao.selectPage(pageObj, wrapper)
        return HandUtils.handleResultByCode(HttpStatus.OK,  mapOf(
            "total" to queryResult.total,
            "items" to queryResult.records
        ), "获取成功")
    }

    fun deleteBanner(bid: Int): Map<String, Any> {
        return if (bannerDao.deleteById(bid) > 0) {
            HandUtils.handleResultByCode(HttpStatus.OK, null, "删除成功")
        } else HandUtils.handleResultByCode(HttpStatus.INTERNAL_SERVER_ERROR, null, "删除失败")
    }

    fun updateBanner(bid: Int, name: String, href: String, image: String): Map<String, Any> {
        if (name.length > 20) return HandUtils.handleResultByCode(HttpStatus.NOT_ACCEPTABLE, null, "标题过长")
        if (bannerDao.selectOne(QueryWrapper<BannerModel>().eq("bid", bid)) == null) return HandUtils.handleResultByCode(HttpStatus.NOT_ACCEPTABLE, null, "轮播不存在")
        val bannerModel = BannerModel()
        ServerBannerManage().updateBanner(bannerModel, bid, name, href, image)
        return if (bannerDao.updateById(bannerModel) > 0) {
            HandUtils.handleResultByCode(HttpStatus.OK, null, "修改成功")
        } else HandUtils.handleResultByCode(HttpStatus.NOT_ACCEPTABLE, null, "修改失败")
    }

    fun createBanner(name: String, href: String, image: String): Map<String, Any> {
        if (name.length > 20) return HandUtils.handleResultByCode(HttpStatus.NOT_ACCEPTABLE, null, "标题过长")
        val bannerModel = BannerModel()
        ServerBannerManage().setBanner(bannerModel, name, href, image)
        return if (bannerDao.insert(bannerModel) > 0) {
            HandUtils.handleResultByCode(HttpStatus.OK, null, "创建成功")
        } else HandUtils.handleResultByCode(HttpStatus.INTERNAL_SERVER_ERROR, null, "创建失败")
    }
}