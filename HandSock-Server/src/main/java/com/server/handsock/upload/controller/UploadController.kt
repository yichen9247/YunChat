package com.server.handsock.upload.controller

import com.server.handsock.common.props.HandProp
import com.server.handsock.common.utils.ConsoleUtils
import com.server.handsock.common.utils.HandUtils
import com.server.handsock.service.AuthService
import com.server.handsock.upload.service.UploadService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.Resource
import org.springframework.core.io.UrlResource
import org.springframework.http.*
import org.springframework.web.bind.annotation.*
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

@RestController
@RequestMapping("/upload")
class UploadController @Autowired constructor(
    handProp: HandProp,
    private val authService: AuthService,
    private val uploadService: UploadService
) {
    private val crossOriginUrls = handProp.origin

    private val referer: String
        get() {
            val request = (Objects.requireNonNull(RequestContextHolder.getRequestAttributes()) as ServletRequestAttributes).request
            return request.getHeader("Referer") ?: "null"
        }

    @Throws(IOException::class)
    private fun uploadFile(entity: HttpEntity<String>, file: MultipartFile, type: String): Map<String, Any> {
        if (!HandUtils.checkImageValidExtension(file) && (type == "avatar" || "images" == type)) return HandUtils.handleResultByCode(HttpStatus.NOT_ACCEPTABLE, null, "不支持的文件类型")
        if (file.isEmpty) return HandUtils.handleResultByCode(HttpStatus.NOT_ACCEPTABLE, null, "禁止上传空文件")
        return uploadService.clientUploadFile(
            file = file,
            type = type,
            headers = entity.headers
        )
    }

    @PostMapping("/file")
    @Throws(IOException::class)
    fun clientUploadFile(entity: HttpEntity<String>, @RequestParam("file") file: MultipartFile): Map<String, Any> {
        return uploadFile(
            file = file,
            type = "files",
            entity = entity
        )
    }

    @PostMapping("/avatar")
    @Throws(IOException::class)
    fun clientUploadAvatar(entity: HttpEntity<String>, @RequestParam("file") file: MultipartFile): Map<String, Any> {
        return uploadFile(
            file = file,
            type = "avatar",
            entity = entity
        )
    }

    @PostMapping("/images")
    @Throws(IOException::class)
    fun clientUploadImages(entity: HttpEntity<String>, @RequestParam("file") file: MultipartFile): Map<String, Any> {
        return uploadFile(
            file = file,
            type = "images",
            entity = entity
        )
    }

    private fun downloadFile(uid: String, md5: String, filename: String, directory: String): ResponseEntity<Resource> {
        try {
            val filePath = Paths.get(directory).resolve(uid).resolve(md5).resolve(filename)
            val resource: Resource = UrlResource(filePath.toUri())
            if (resource.exists()) {
                val encodedFilename = URLEncoder.encode(filename, StandardCharsets.UTF_8).replace("\\+".toRegex(), "%20")
                return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''$encodedFilename")
                    .contentType(MediaType.parseMediaType(Files.probeContentType(filePath)))
                    .body(resource)
            }
        } catch (e: Exception) {
            ConsoleUtils.printErrorLog(e)
        }
        return ResponseEntity.notFound().build()
    }

    @GetMapping("/download/{type}/{uid}/{md5}/{filename:.+}")
    fun download(
        request: HttpServletRequest,
        @PathVariable type: String?,
        @PathVariable uid: String,
        @PathVariable md5: String,
        @PathVariable filename: String
    ): ResponseEntity<Resource> {
        val referer = referer
        if (!referer.startsWith(crossOriginUrls!!)) {
            val token = request.getHeader("token")
            val requestUid = request.getHeader("uid")
            if (token == null || requestUid == null || !authService.validClientTokenByRequest(request)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
            }
        }

        val directory = when (type) {
            "file" -> UPLOAD_DIRECTORIES[0]
            "avatar" -> UPLOAD_DIRECTORIES[1]
            "images" -> UPLOAD_DIRECTORIES[2]
            else -> return ResponseEntity.status(HttpStatus.BAD_REQUEST).build()
        }
        return downloadFile(
            uid = uid,
            md5 = md5,
            filename = filename,
            directory = directory
        )
    }

    companion object {
        private val UPLOAD_DIRECTORIES = arrayOf("upload/files/", "upload/avatar/", "upload/images/")
    }
}