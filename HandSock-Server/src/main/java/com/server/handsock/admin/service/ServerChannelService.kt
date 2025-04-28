package com.server.handsock.admin.service

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.server.handsock.admin.man.ServerChannelManage
import com.server.handsock.common.dao.ChannelDao
import com.server.handsock.common.model.ChannelModel
import com.server.handsock.common.utils.HandUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class ServerChannelService @Autowired constructor(private val channelDao: ChannelDao) {
    fun getChanList(page: Int, limit: Int): Map<String, Any> {
        val wrapper = QueryWrapper<ChannelModel>()
        val pageObj = Page<ChannelModel>(page.toLong(), limit.toLong())
        val queryResult = channelDao.selectPage(pageObj, wrapper)
        return HandUtils.handleResultByCode(
            HttpStatus.OK,  mapOf(
            "total" to queryResult.total,
            "items" to queryResult.records
        ), "获取成功")
    }

    fun deleteChan(gid: Long): Map<String, Any> {
        if (gid == 0L) return HandUtils.handleResultByCode(HttpStatus.NOT_ACCEPTABLE, null, "主频道不可操作")
        return if (channelDao.deleteById(gid) > 0) {
            HandUtils.handleResultByCode(HttpStatus.OK, null, "删除成功")
        } else HandUtils.handleResultByCode(HttpStatus.NOT_ACCEPTABLE, null, "删除失败")
    }

    fun updateChan(gid: Long, name: String, avatar: String, notice: String, aiRole: Boolean?): Map<String, Any> {
        if (name.length > 10 || notice.length > 100 || avatar.length > 200) return HandUtils.handleResultByCode(HttpStatus.NOT_ACCEPTABLE, null, "参数过长")
        if (channelDao.selectOne(QueryWrapper<ChannelModel>().eq("gid", gid)) == null) return HandUtils.handleResultByCode(HttpStatus.NOT_ACCEPTABLE, null, "频道不存在")
        val channelModel = ChannelModel()
        ServerChannelManage().setChan(channelModel, gid, name, avatar, notice, aiRole)
        return if (channelDao.updateById(channelModel) > 0) {
            HandUtils.handleResultByCode(HttpStatus.OK, null, "修改成功")
        } else HandUtils.handleResultByCode(HttpStatus.NOT_ACCEPTABLE, null, "修改失败")
    }

    fun createChan(gid: Long, name: String, avatar: String, notice: String, aiRole: Boolean): Map<String, Any> {
        if (name.length > 10 || notice.length > 100 || avatar.length > 200) return HandUtils.handleResultByCode(HttpStatus.NOT_ACCEPTABLE, null, "参数过长")
        if (channelDao.selectOne(QueryWrapper<ChannelModel>().eq("gid", gid)) != null) return HandUtils.handleResultByCode(HttpStatus.NOT_ACCEPTABLE, null, "频道已存在")
        val channelModel = ChannelModel()
        ServerChannelManage().setChan(channelModel, gid, name, avatar, notice, aiRole)
        return if (channelDao.insert(channelModel) > 0) {
            HandUtils.handleResultByCode(HttpStatus.OK, null, "创建成功")
        } else HandUtils.handleResultByCode(HttpStatus.INTERNAL_SERVER_ERROR, null, "创建失败")
    }

    fun updateChanOpenStatus(gid: Long, status: Int): Map<String, Any> {
        if (gid == 0L) return HandUtils.handleResultByCode(HttpStatus.NOT_ACCEPTABLE, null, "主频道不可操作")
        val channelModel = ChannelModel()
        ServerChannelManage().updateChanOpenStatus(channelModel, gid, status)
        return if (channelDao.updateById(channelModel) > 0) {
            HandUtils.handleResultByCode(HttpStatus.OK, null, "设置成功")
        } else HandUtils.handleResultByCode(HttpStatus.INTERNAL_SERVER_ERROR, null, "设置失败")
    }

    fun updateChanActiveStatus(gid: Long, status: Int): Map<String, Any> {
        if (gid == 0L) return HandUtils.handleResultByCode(HttpStatus.NOT_ACCEPTABLE, null, "主频道不可操作")
        val channelModel = ChannelModel()
        ServerChannelManage().updateChanActiveStatus(channelModel, gid, status)
        return if (channelDao.updateById(channelModel) > 0) {
            HandUtils.handleResultByCode(HttpStatus.OK, null, "设置成功")
        } else HandUtils.handleResultByCode(HttpStatus.INTERNAL_SERVER_ERROR, null, "设置失败")
    }
}
