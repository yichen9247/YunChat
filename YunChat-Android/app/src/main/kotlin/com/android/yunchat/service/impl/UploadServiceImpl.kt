package com.android.yunchat.service.impl

import android.app.Activity
import android.net.Uri
import androidx.activity.result.ActivityResult
import com.android.yunchat.enmu.FileTypeEnum
import com.android.yunchat.request.model.UploadRequestViewModel
import com.android.yunchat.service.UploadService
import com.android.yunchat.service.instance.requestServiceInstance
import com.xuexiang.xutil.XUtil
import top.chengdongqing.weui.core.ui.components.toast.ToastState
import top.chengdongqing.weui.core.utils.showToast

/**
 * @name 上传服务实现类
 * @author yichen9247
 */
class UploadServiceImpl: UploadService {
    /**
     * @name 处理上传文件
     * @param uri 文件uri
     * @param type 文件类型
     * @onSuccess 上传成功回调
     * @param toastState 提示状态
     */
    override suspend fun handleUploadFile(
        uri: Uri,
        type: FileTypeEnum,
        toastState: ToastState,
        onSuccess: (path: String) -> Unit
    ) {
        val uploadVM = UploadRequestViewModel()
        try {
            uploadVM.showUploadDialog(toastState)
            val response = uploadVM.uploadFile(uri, type)
            response?.let {
                requestServiceInstance.checkResponseResult(
                    message = "上传失败",
                    response = response,
                    onSuccess = {
                        it.data?.path?.let {
                            onSuccess(it)
                        }
                    }
                )
            }
        } finally {
            toastState.hide()
        }
    }

    /**
     * @name 处理选择文件
     * @param result 选择文件结果
     * @param onSuccess 选择文件成功回调
     */
    override suspend fun handleSelectFile(
        result: ActivityResult,
        toastState: ToastState,
        onSuccess: (path: String) -> Unit
    ) {
        if (result.resultCode != Activity.RESULT_OK) return
        val uri = result.data?.data ?: run {
            XUtil.getContext().showToast("选择文件失败")
            return
        }
        handleUploadFile(uri, FileTypeEnum.FILE, toastState, onSuccess)
    }

    /**
     * @name 处理选择图片
     * @param uri 图片uri
     * @param onSuccess 选择图片成功回调
     */
    override suspend fun handleSelectImage(
        uri: Uri,
        toastState: ToastState,
        onSuccess: (path: String) -> Unit
    ) {
        handleUploadFile(uri, FileTypeEnum.IMAGE, toastState, onSuccess)
    }

    /**
     * @name 处理选择头像
     * @param uri 图片uri
     * @param onSuccess 选择图片成功回调
     */
    override suspend fun handleSelectAvatar(
        uri: Uri,
        toastState: ToastState,
        onSuccess: (path: String) -> Unit
    ) {
        handleUploadFile(uri, FileTypeEnum.AVATAR, toastState, onSuccess)
    }


    /**
     * @name 处理选择视频
     * @param uri 图片uri
     * @param onSuccess 选择图片成功回调
     */
    override suspend fun handleSelectVideo(
        uri: Uri,
        toastState: ToastState,
        onSuccess: (path: String) -> Unit
    ) {
        handleUploadFile(uri, FileTypeEnum.VIDEO, toastState, onSuccess)
    }
}