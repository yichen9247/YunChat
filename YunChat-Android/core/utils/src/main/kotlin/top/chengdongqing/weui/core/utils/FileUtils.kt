package top.chengdongqing.weui.core.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

fun deleteFile(file: File): Boolean {
    if (file.isDirectory) {
        file.listFiles()?.forEach { child ->
            deleteFile(child)
        }
    }
    return file.delete()
}

suspend fun calculateFileSize(file: File): Long = withContext(Dispatchers.IO) {
    if (file.isFile) {
        file.length()
    } else if (file.isDirectory) {
        val children = file.listFiles()
        var totalSize: Long = 0
        if (children != null) {
            for (child in children) {
                totalSize += calculateFileSize(child)
            }
        }
        totalSize
    } else 0
}

fun Context.getFileProviderUri(file: File): Uri {
    return FileProvider.getUriForFile(this, "$packageName.provider", file)
}

fun Context.shareFile(file: File, mimeType: String = "image/*") {
    val sharingUri = getFileProviderUri(file)
    val intent = Intent(Intent.ACTION_SEND).apply {
        this.type = mimeType
        putExtra(Intent.EXTRA_STREAM, sharingUri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    startActivity(intent)
}