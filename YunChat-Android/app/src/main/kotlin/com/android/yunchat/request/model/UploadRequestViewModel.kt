package com.android.yunchat.request.model

import android.content.ContentResolver
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import com.android.yunchat.YunChat
import com.android.yunchat.enmu.FileTypeEnum
import com.android.yunchat.model.ResultModel
import com.android.yunchat.model.UploadResultModel
import com.android.yunchat.request.repository.impl.UploadRequestRepositoryImpl
import com.xuexiang.xutil.XUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import top.chengdongqing.weui.core.ui.components.toast.ToastIcon
import top.chengdongqing.weui.core.ui.components.toast.ToastState
import top.chengdongqing.weui.core.utils.showToast
import java.io.File
import java.io.IOException

class UploadRequestViewModel : AndroidViewModel(YunChat.instance) {
    private val application = YunChat.instance
    private val uploadRequestRepository by lazy { UploadRequestRepositoryImpl() }
    private val contentResolver: ContentResolver get() = application.contentResolver

    fun showUploadDialog(toastState: ToastState) {
        toastState.show(
            title = "正在上传中",
            icon = ToastIcon.LOADING
        )
    }

    suspend fun uploadFile(uri: Uri, type: FileTypeEnum): ResultModel<UploadResultModel>? {
        return withContext(Dispatchers.IO) {
            try {
                val (fileName, mimeType) = getFileMetadata(uri) ?: return@withContext null
                val tempFile = createTempFile(uri) ?: return@withContext null
                val requestFile = tempFile.asRequestBody(mimeType.toMediaType())
                val body = MultipartBody.Part.createFormData("file", fileName, requestFile)
                return@withContext when (type) {
                    FileTypeEnum.FILE -> uploadRequestRepository.uploadFile(body)
                    FileTypeEnum.IMAGE -> uploadRequestRepository.uploadImage(body)
                    FileTypeEnum.VIDEO -> uploadRequestRepository.uploadVideo(body)
                    FileTypeEnum.AVATAR -> uploadRequestRepository.uploadAvatar(body)
                    else -> null
                }
            } catch (_: Exception) {
                XUtil.getContext().showToast("上传文件失败")
                null
            }
        }
    }

    private fun getFileMetadata(uri: Uri): Pair<String, String>? {
        return contentResolver.query(uri, arrayOf(
            MediaStore.MediaColumns.DISPLAY_NAME,
            MediaStore.MediaColumns.MIME_TYPE
        ), null, null, null )?.use { cursor ->
            if (cursor.moveToFirst()) {
                val nameIndex = cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME)
                val mimeTypeIndex = cursor.getColumnIndex(MediaStore.MediaColumns.MIME_TYPE)

                if (nameIndex != -1 && mimeTypeIndex != -1) {
                    val fileName = cursor.getString(nameIndex)
                    val mimeType = cursor.getString(mimeTypeIndex)
                    fileName to (mimeType ?: DEFAULT_MIME_TYPE)
                } else null
            } else null
        }
    }

    private fun createTempFile(uri: Uri): File? {
        return try {
            contentResolver.openInputStream(uri)?.use { inputStream ->
                File.createTempFile("upload_", null).apply {
                    outputStream().use { output -> inputStream.copyTo(output) }
                    deleteOnExit()
                }
            }
        } catch (_: IOException) {
            XUtil.getContext().showToast("创建文件缓存失败")
            null
        }
    }

    companion object {
        private const val DEFAULT_MIME_TYPE = "application/octet-stream"
    }
}