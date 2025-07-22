package com.server.yunchat.upload.controller

import com.server.yunchat.builder.props.YunChatProps
import com.server.yunchat.builder.service.AuthObjService
import com.server.yunchat.builder.types.UploadType
import com.server.yunchat.upload.service.DownloadService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.Resource
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/download")
class DownloadController @Autowired constructor(
    private val yunChatProps: YunChatProps,
    private val authObjService: AuthObjService,
    private val downloadService: DownloadService,
) {

    @GetMapping("/{type}/{uid}/{md5}/{filename:.+}")
    fun download(
        request: HttpServletRequest,
        @PathVariable md5: String,
        @PathVariable uid: String,
        @PathVariable type: String,
        @PathVariable filename: String
    ): ResponseEntity<Resource> {
        val directory = getUploadDirectory(type) ?: return ResponseEntity.badRequest().build()
        if (!isAuthorizedRequest(request)) return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        return downloadService.downloadFile(uid, md5, filename, directory)
    }

    private fun getUploadDirectory(type: String?): String? {
        return when (type) {
            UploadType.FILE -> UPLOAD_DIRECTORIES[0]
            UploadType.IMAGE -> UPLOAD_DIRECTORIES[1]
            UploadType.VIDEO -> UPLOAD_DIRECTORIES[2]
            UploadType.AVATAR -> UPLOAD_DIRECTORIES[3]
            else -> null
        }
    }

    private fun isAuthorizedRequest(request: HttpServletRequest): Boolean {
        val requestStatus = authObjService.validClientTokenByRequest(request)
        val crossOriginUrls = yunChatProps.origin ?: return requestStatus
        val referer = request.getHeader("Referer")?.takeIf { it.isNotBlank() }
        val hasValidReferer = referer?.let { ref ->
            crossOriginUrls.any { ref.startsWith(it) }
        } ?: false
        return hasValidReferer || requestStatus
    }

    companion object {
        private val UPLOAD_DIRECTORIES = arrayOf(
            "upload/file/", "upload/image/", "upload/video", "upload/avatar/"
        )
    }
}