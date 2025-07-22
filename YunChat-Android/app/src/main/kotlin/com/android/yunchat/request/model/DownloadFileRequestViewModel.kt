package com.android.yunchat.request.model

import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import com.android.yunchat.R
import com.android.yunchat.request.repository.impl.DownloadRepositoryImpl
import com.xuexiang.xutil.XUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import top.chengdongqing.weui.core.utils.showToast
import java.io.File
import java.io.IOException

class DownloadFileRequestViewModel : ViewModel() {
    /**
     * 创建目录（如果不存在）
     * @param directory 需要创建的目录
     * @throws IOException 当目录创建失败时抛出
     */
    private fun createDirectoryIfNeeded(directory: File) {
        if (!directory.exists() && !directory.mkdirs()) {
            throw IOException("创建文件夹失败")
        }
    }

    suspend fun downloadFile(filename: String, savePath: String): Uri? = withContext(Dispatchers.IO) {
        val context = XUtil.getContext()
        val fileName = File(filename).name
        val folderName = context.getString(R.string.app_name)
        val downloadDir = File(Environment.getExternalStoragePublicDirectory(savePath), folderName)
        val downloadFile = File(downloadDir, fileName)
        try {
            createDirectoryIfNeeded(downloadDir)
            if (downloadFile.exists()) {
                return@withContext getFileUri(context, downloadFile)
            }
            val responseBody = downloadRepository.downloadFile(filename)
                ?: throw IOException("下载文件失败")
            responseBody.byteStream().use { inputStream ->
                downloadFile.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            getFileUri(context, downloadFile)
        } catch (e: Exception) {
            if (downloadFile.exists() && downloadFile.length() == 0L) {
                downloadFile.delete()
            }
            XUtil.getContext().showToast(e.message ?: "下载文件失败")
            null
        }
    }

    private fun getFileUri(context: Context, file: File): Uri {
        return FileProvider.getUriForFile(context,
            "${context.packageName}.provider", file
        )
    }

    companion object {
        private val downloadRepository by lazy {
            DownloadRepositoryImpl()
        }
    }
}