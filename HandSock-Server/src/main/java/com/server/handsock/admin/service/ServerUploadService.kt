package com.server.handsock.admin.service

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.server.handsock.common.dao.UploadDao
import com.server.handsock.common.model.UploadModel
import com.server.handsock.common.utils.ConsoleUtils
import com.server.handsock.common.utils.HandUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import java.io.File

@Service
class ServerUploadService @Autowired constructor(private val uploadDao: UploadDao) {
    fun getUploadList(page: Int, limit: Int): Map<String, Any> {
        val pageObj = Page<UploadModel>(page.toLong(), limit.toLong())
        val wrapper = QueryWrapper<UploadModel>().orderByDesc("time")
        val queryResult = uploadDao.selectPage(pageObj, wrapper)
        return HandUtils.handleResultByCode(HttpStatus.OK,  mapOf(
            "total" to queryResult.total,
            "items" to queryResult.records
        ), "获取成功")
    }

    fun deleteUpload(fid: String): Map<String, Any> {
        deleteUploadFile(fid)
        return if (uploadDao.deleteById(fid) > 0) {
            HandUtils.handleResultByCode(HttpStatus.OK, null, "删除成功")
        } else HandUtils.handleResultByCode(HttpStatus.INTERNAL_SERVER_ERROR, null, "删除失败")
    }

    private fun deleteUploadFile(fid: String?) {
        val serverUploadModel = uploadDao.selectById(fid)
        val uploadFile = File("upload/${serverUploadModel.type}/${serverUploadModel.path}")
        if (uploadFile.exists() && uploadFile.delete()) {
            ConsoleUtils.printInfoLog("File deleted $fid")
            ConsoleUtils.printInfoLog("$serverUploadModel.path deleted")
        } else ConsoleUtils.printErrorLog(Exception("File delete failed $fid"))
    }
}
