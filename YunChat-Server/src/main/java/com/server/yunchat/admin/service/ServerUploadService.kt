package com.server.yunchat.admin.service

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.server.yunchat.builder.dao.UploadDao
import com.server.yunchat.builder.data.ResultModel
import com.server.yunchat.builder.model.UploadModel
import com.server.yunchat.builder.utils.ConsoleUtils
import com.server.yunchat.builder.utils.HandUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import java.io.File

@Service
class ServerUploadService @Autowired constructor(private val uploadDao: UploadDao) {
    fun getUploadList(page: Int, limit: Int): ResultModel {
        val pageObj = Page<UploadModel>(page.toLong(), limit.toLong())
        val wrapper = QueryWrapper<UploadModel>().orderByDesc("time")
        val queryResult = uploadDao.selectPage(pageObj, wrapper)
        return HandUtils.handleResultByCode(HttpStatus.OK,  mapOf(
            "total" to queryResult.total,
            "items" to queryResult.records
        ), "获取成功")
    }

    fun deleteUpload(fid: String): ResultModel {
        return try {
            deleteUploadFile(fid)
            if (uploadDao.deleteById(fid) > 0) {
                HandUtils.handleResultByCode(HttpStatus.OK, null, "删除成功")
            } else HandUtils.handleResultByCode(HttpStatus.INTERNAL_SERVER_ERROR, null, "删除失败")
        } catch (e: Exception) {
            HandUtils.handleResultByCode(HttpStatus.INTERNAL_SERVER_ERROR, null, e.message.toString())
        }
    }

    private fun deleteUploadFile(fid: String) {
        val serverUploadModel = uploadDao.selectById(fid)
        val uploadFile = File("upload/${serverUploadModel.type}/${serverUploadModel.path}")
        if (uploadFile.exists() && uploadFile.delete()) {
            ConsoleUtils.printCustomLogs(
                hash = fid,
                address = "无",
                content = "File deleted",
                message = "upload/${serverUploadModel.type}/${serverUploadModel.path}"
            )
        } else ConsoleUtils.printErrorLog("File delete failed $fid")
    }
}
