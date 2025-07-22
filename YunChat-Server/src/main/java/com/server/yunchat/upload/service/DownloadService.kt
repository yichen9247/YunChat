package com.server.yunchat.upload.service

import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import java.io.IOException
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

@Service
class DownloadService {
    private fun detectContentType(filePath: Path): MediaType {
        return try {
            Files.probeContentType(filePath)?.let {
                MediaType.parseMediaType(it)
            } ?: MediaType.APPLICATION_OCTET_STREAM
        } catch (e: IOException) {
            MediaType.APPLICATION_OCTET_STREAM
        }
    }

    fun downloadFile(uid: String, md5: String, filename: String, directory: String): ResponseEntity<Resource> {
        val baseDir = Paths.get(directory).normalize()
        val filePath = baseDir.resolve(uid).resolve(md5).resolve(filename).normalize()
        if (!filePath.startsWith(baseDir)) return ResponseEntity.badRequest().build()
        val resource = FileSystemResource(filePath)
        if (!resource.exists()) return ResponseEntity.notFound().build()

        return try {
            val contentLength = Files.size(filePath)
            val contentType = detectContentType(filePath)
            val encodedFilename = URLEncoder.encode(filename, StandardCharsets.UTF_8).replace("+", "%20")
            ResponseEntity.ok()
                .contentType(contentType)
                .header(HttpHeaders.CONTENT_LENGTH, contentLength.toString())
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''$encodedFilename")
                .body(resource)
        } catch (e: IOException) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }
}