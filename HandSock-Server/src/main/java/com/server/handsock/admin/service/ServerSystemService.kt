package com.server.handsock.admin.service

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.server.handsock.admin.man.ServerSystemManage
import com.server.handsock.common.dao.SystemDao
import com.server.handsock.common.model.SystemModel
import com.server.handsock.common.utils.HandUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class ServerSystemService @Autowired constructor(private val systemDao: SystemDao) {
    fun getSystemKeyStatus(key: String): Boolean {
        val systemModel = systemDao.selectOne(QueryWrapper<SystemModel>().eq("name", key))
        if (systemModel != null) return systemModel.value.equals("open")
        return false
    }

    fun setSystemTabooStatus(value: String): Map<String, Any> {
        val systemModel = systemDao.selectOne(QueryWrapper<SystemModel>().eq("name", "taboo"))
        val result = ServerSystemManage().setSystemKeyStatus(systemModel, value)
        return if (systemDao.updateById(systemModel) > 0) {
            HandUtils.handleResultByCode(HttpStatus.OK, result, "设置状态成功")
        } else HandUtils.handleResultByCode(HttpStatus.INTERNAL_SERVER_ERROR, null, "设置状态失败")
    }

    fun setSystemUploadStatus(value: String): Map<String, Any> {
        val systemModel = systemDao.selectOne(QueryWrapper<SystemModel>().eq("name", "upload"))
        val result = ServerSystemManage().setSystemKeyStatus(systemModel, value)
        return if (systemDao.updateById(systemModel) > 0) {
            HandUtils.handleResultByCode(HttpStatus.OK, result, "设置状态成功")
        } else HandUtils.handleResultByCode(HttpStatus.INTERNAL_SERVER_ERROR, null, "设置状态失败")
    }

    fun setSystemRegisterStatus(value: String): Map<String, Any> {
        val systemModel = systemDao.selectOne(QueryWrapper<SystemModel>().eq("name", "register"))
        val result = ServerSystemManage().setSystemKeyStatus(systemModel, value)
        return if (systemDao.updateById(systemModel) > 0) {
            HandUtils.handleResultByCode(HttpStatus.OK, result, "设置状态成功")
        } else HandUtils.handleResultByCode(HttpStatus.INTERNAL_SERVER_ERROR, null, "设置状态失败")
    }

    fun setSystemConfigValue(name: String, value: String): Map<String, Any> {
        val systemModel = systemDao.selectOne(QueryWrapper<SystemModel>().eq("name", name))
            ?: return HandUtils.handleResultByCode(HttpStatus.NOT_ACCEPTABLE, null, "未知选项")
        val result = ServerSystemManage().setSystemKeyStatus(systemModel, value)
        return if (systemDao.updateById(systemModel) > 0) {
            HandUtils.handleResultByCode(HttpStatus.OK, result, "设置成功")
        } else HandUtils.handleResultByCode(HttpStatus.INTERNAL_SERVER_ERROR, null, "设置失败")
    }

    fun checkAppUpdate(version: String): Map<String, Any> {
        val versionModel = systemDao.selectOne(QueryWrapper<SystemModel>().eq("name", "version"))
        return if (version != versionModel.value) {
            val downloadModel = systemDao.selectOne(QueryWrapper<SystemModel>().eq("name", "download"))
            HandUtils.handleResultByCode(HttpStatus.CREATED, mapOf(
                "version" to versionModel.value,
                "download" to downloadModel.value
            ), "有新版本可更新")
        } else HandUtils.handleResultByCode(HttpStatus.OK, null, "已是最新版本")
    }

    fun getAllSystemConfig(): Map<String, Any> {
        val serverSystemModelList = systemDao.selectList(null)
        return HandUtils.handleResultByCode(HttpStatus.OK, serverSystemModelList, "获取成功")
    }
}
