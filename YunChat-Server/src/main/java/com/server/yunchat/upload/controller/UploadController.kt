package com.server.yunchat.upload.controller

import com.server.yunchat.builder.data.ResultModel
import com.server.yunchat.builder.types.UploadType
import com.server.yunchat.builder.utils.HandUtils
import com.server.yunchat.upload.service.UploadService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/upload")
class UploadController @Autowired constructor(private val uploadService: UploadService) {

    companion object {
        const val ERROR_EMPTY_FILE = "禁止上传空文件"
        const val ERROR_UNSUPPORTED_TYPE = "不支持的文件类型"
    }

    @PostMapping("/{type}")
    fun uploadFile(
        request: HttpServletRequest,
        @PathVariable type: String, @RequestParam("file") file: MultipartFile
    ): ResultModel {
        return uploadFile(type, file, request)
    }

    private fun uploadFile(
        type: String,
        file: MultipartFile,
        request: HttpServletRequest
    ): ResultModel {
        if (file.isEmpty)
            return HandUtils.handleResultByCode(HttpStatus.NOT_ACCEPTABLE, null, ERROR_EMPTY_FILE)
        when (type) {
            UploadType.FILE -> Unit
            UploadType.VIDEO -> {
                if (!HandUtils.checkVideoValidExtension(file))
                    return HandUtils.handleResultByCode(HttpStatus.NOT_ACCEPTABLE, null, ERROR_UNSUPPORTED_TYPE)
            }
            UploadType.IMAGE, UploadType.AVATAR -> {
                if (!HandUtils.checkImageValidExtension(file))
                    return HandUtils.handleResultByCode(HttpStatus.NOT_ACCEPTABLE, null, ERROR_UNSUPPORTED_TYPE)
            }
            else -> return HandUtils.handleResultByCode(HttpStatus.NOT_ACCEPTABLE, null, ERROR_UNSUPPORTED_TYPE)
        }

        return uploadService.clientUploadFile(file, type, request)
    }
}