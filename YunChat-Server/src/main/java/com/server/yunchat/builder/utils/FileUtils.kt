package com.server.yunchat.builder.utils

import org.springframework.web.multipart.MultipartFile
import java.io.File

/**
 * @name 文件工具类
 * @author yichen9247
 */
object FileUtils  {
    fun multipartFileToFile(multipartFile: MultipartFile): File {
        val fileName = multipartFile.originalFilename ?: "default_name.tmp"
        val targetFile = File(System.getProperty("java.io.tmpdir"), fileName)
        multipartFile.transferTo(targetFile)
        return targetFile
    }
}