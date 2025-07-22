package com.android.yunchat.request.model

import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import com.android.yunchat.request.repository.impl.DownloadRepositoryImpl
import com.xuexiang.xutil.XUtil
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class DownloadRequestViewModel : ViewModel() {
    private val downloadRepository by lazy {
        DownloadRepositoryImpl()
    }

    private fun ensureDirectoryExists(file: File): Boolean {
        val parentDir = file.parentFile
        return if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs() // 创建所有必要的父目录
        } else {
            true
        }
    }

    suspend fun downloadFile(filename: String): Uri? {
        val context = XUtil.getContext()
        val cacheDir = context.cacheDir
        val safeFilename = filename.replace("/", "_").replace("\\", "_")
        val cachedFile = File(cacheDir, safeFilename)

        if (!ensureDirectoryExists(cachedFile)) {
            throw IOException("Failed to create necessary directories for file: ${cachedFile.path}")
        }

        if (cachedFile.exists()) {
            return FileProvider.getUriForFile(context, "${context.packageName}.provider", cachedFile)
        }

        return try {
            val responseBody = downloadRepository.downloadFile(filename) ?: run {
                throw IOException("Download response is null")
            }
            cachedFile.outputStream().use { outputStream ->
                responseBody.byteStream().use { inputStream ->
                    copyStream(inputStream, outputStream)
                }
            }
            FileProvider.getUriForFile(context, "${context.packageName}.provider", cachedFile)
        } catch (_: Exception) {
            null
        }
    }

    private fun copyStream(input: InputStream, output: OutputStream) {
        val buffer = ByteArray(8192)
        var bytesRead: Int
        try {
            while (input.read(buffer).also { bytesRead = it } != -1) {
                output.write(buffer, 0, bytesRead)
            }
            output.flush()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            input.close()
            output.close()
        }
    }
}