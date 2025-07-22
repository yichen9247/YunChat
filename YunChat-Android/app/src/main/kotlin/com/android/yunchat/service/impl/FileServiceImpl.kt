package com.android.yunchat.service.impl

import android.content.Context
import android.net.Uri
import android.os.Environment
import com.android.yunchat.request.model.DownloadFileRequestViewModel
import com.android.yunchat.service.FileService
import com.android.yunchat.service.instance.systemServiceInstance
import com.xuexiang.xutil.XUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import top.chengdongqing.weui.core.data.model.MediaItem
import top.chengdongqing.weui.core.data.model.MediaType
import top.chengdongqing.weui.core.ui.components.mediapreview.previewMedias
import top.chengdongqing.weui.core.ui.components.toast.ToastIcon
import top.chengdongqing.weui.core.ui.components.toast.ToastState
import top.chengdongqing.weui.core.utils.showToast
import kotlin.time.Duration

/**
 * @name 文件服务实现类
 * @author yichen9247
 */
class FileServiceImpl: FileService {
    /**
     * @name 保存图片
     * @param content 图片地址
     * @param toastState 提示状态
     */
    override suspend fun saveImage(
        content: String,
        toastState: ToastState
    ) = downloadAndHandle(
        content = content,
        toastState = toastState,
        downloader = { DownloadFileRequestViewModel().downloadFile(
            filename = it,
            savePath = Environment.DIRECTORY_PICTURES
        ) },
        onSuccess = { uri ->
            XUtil.getContext().showToast("保存图片成功")
        }
    )

    /**
     * @name 打开系统文件预览
     * @param content 文件地址
     * @param toastState 提示状态
     */
    override suspend fun openSystemFilePreview(
        content: String,
        toastState: ToastState
    ) = downloadAndHandle(
        content = content,
        toastState = toastState,
        onSuccess = { uri ->
            systemServiceInstance.openFileWithDevice(uri)
        },
        downloader = { DownloadFileRequestViewModel().downloadFile(
            filename = it,
            savePath = Environment.DIRECTORY_DOWNLOADS
        ) }
    )

    private suspend fun downloadAndHandle(
        content: String,
        toastState: ToastState,
        onSuccess: suspend (Uri) -> Unit,
        downloader: suspend (String) -> Uri?
    ) = withContext(Dispatchers.Main.immediate){
        toastState.show(
            mask = true,
            title = "正在下载中",
            icon = ToastIcon.LOADING,
            duration = Duration.INFINITE
        )
        try {
            val uri = withContext(Dispatchers.IO) {
                downloader(content)
            }
            if (uri != null) onSuccess(uri)
        } catch (_: Exception) {
            XUtil.getContext().showToast("下载失败")
        } finally {
            toastState.hide()
        }
    }

    /**
     * @name 打开媒体预览
     * @param content 文件地址
     * @param context 上下文
     * @param mediaType 文件类型
     * @param toastState 提示状态
     */
    override suspend fun openMediaPreview(
        content: String,
        context: Context,
        mediaType: MediaType,
        toastState: ToastState
    ) = downloadAndHandle(
        content = content,
        toastState = toastState,
        onSuccess = { uri ->
            context.previewMedias(listOf(
                MediaItem(
                    uri = uri,
                    mimeType = "*/*",
                    filename = "文件预览",
                    mediaType = mediaType
                )
            ))
        },
        downloader = { DownloadFileRequestViewModel().downloadFile(
            filename = it,
            savePath = Environment.DIRECTORY_DOWNLOADS
        ) }
    )
}