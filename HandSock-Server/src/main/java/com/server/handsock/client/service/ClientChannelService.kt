package com.server.handsock.client.service

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.server.handsock.common.dao.ChannelDao
import com.server.handsock.common.model.ChannelModel
import com.server.handsock.common.utils.HandUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class ClientChannelService @Autowired constructor(private val channelDao: ChannelDao) {
    fun searchAllGroup(): Map<String, Any> {
        val channelModelList = channelDao.selectList(QueryWrapper<ChannelModel>().eq("active", 1))
        val history: MutableList<Map<String, Any>> = ArrayList()
        for (clientChannelModel in channelModelList) {
            history.add(object : HashMap<String, Any>() {
                init {
                    clientChannelModel.gid?.let { put("gid", it) }
                }
            })
        }
        val result = HandUtils.handleResultByCode(HttpStatus.OK, channelModelList, "获取成功").apply {
            "latest" to history
        }
        return result
    }

    fun searchGroupByGid(gid: Long): Map<String, Any> {
        val groupSelect = if (gid == 0L) {
            channelDao.selectOne(QueryWrapper<ChannelModel>().eq("home", 1))
        } else channelDao.selectOne(QueryWrapper<ChannelModel>().eq("gid", gid))
        return if (groupSelect == null) {
            HandUtils.handleResultByCode(HttpStatus.NOT_ACCEPTABLE, null, "未找到频道")
        } else HandUtils.handleResultByCode(HttpStatus.OK, groupSelect, "获取成功")
    }

    fun getChanOpenStatus(gid: Long): Boolean {
        val status = channelDao.selectOne(QueryWrapper<ChannelModel>().eq("gid", gid)).open
        return status == 1
    }
}
