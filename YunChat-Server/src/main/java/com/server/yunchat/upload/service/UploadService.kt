package com.server.yunchat.upload.service

import com.aliyun.oss.OSS
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.qcloud.cos.COSClient
import com.qcloud.cos.model.PutObjectRequest
import com.server.yunchat.admin.service.ServerSystemService
import com.server.yunchat.builder.dao.UploadDao
import com.server.yunchat.builder.data.ResultModel
import com.server.yunchat.builder.model.UploadModel
import com.server.yunchat.builder.props.AliOssProps
import com.server.yunchat.builder.props.TencentCosProps
import com.server.yunchat.builder.props.YunChatProps
import com.server.yunchat.builder.types.StorageType
import com.server.yunchat.builder.types.UserAuthType
import com.server.yunchat.builder.utils.*
import com.server.yunchat.client.dao.ClientUserDao
import com.server.yunchat.client.mod.ClientUserModel
import com.server.yunchat.service.impl.ClientServiceImpl
import com.server.yunchat.service.impl.EncryptServiceImpl
import com.server.yunchat.upload.man.UploadManage
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*

@Service
class UploadService @Autowired constructor(
    private val ossClient: OSS,
    private val uploadDao: UploadDao,
    private val cosClient: COSClient,
    private val aliOssProps: AliOssProps,
    private val yunChatProps: YunChatProps,
    private val clientUserDao: ClientUserDao,
    private val tencentCosProps: TencentCosProps,
    private val clientServiceImpl: ClientServiceImpl,
    private val encryptServiceImpl: EncryptServiceImpl,
    private val serverSystemService: ServerSystemService
) {
    fun clientUploadFile(@RequestParam("file") file: MultipartFile, type: String, request: HttpServletRequest): ResultModel {
        try {
            val authorization = request.getHeader("authorization") ?: ""
            encryptServiceImpl.decryptAuthorization(authorization, true)?.let {
                if (!serverSystemService.getSystemKeyStatus("upload") && clientUserDao.selectOne(QueryWrapper<ClientUserModel>().eq("uid", it.uid)).permission != UserAuthType.ADMIN_AUTHENTICATION) {
                    return HandUtils.handleResultByCode(HttpStatus.FORBIDDEN, null, "上传权限未开放")
                }
                val uploadModel = UploadModel()
                val fileName = file.originalFilename
                val time = HandUtils.encodeStringToMD5(HandUtils.formatTimeForString("yyyy-MM-dd-HH-mm-ss"))
                val uploadDir = createUploadDirectory(
                    type = type,
                    time = time,
                    uid = it.uid.toString()
                )
                var filePath = "${it.uid}/$time/$fileName"
                when(yunChatProps.storageMod) {
                    StorageType.STORAGE_TENCENT -> {
                        val tempFile = FileUtils.multipartFileToFile(file)
                        try {
                            val fileUPath = "$type/$filePath"
                            val bucket = tencentCosProps.bucket
                            cosClient.putObject(PutObjectRequest(bucket, "handsock/$fileUPath", tempFile))
                            filePath = "https://$bucket.cos.${tencentCosProps.region}.myqcloud.com/handsock/$fileUPath"
                        } finally {
                            tempFile.delete() // 确保上传完成后删除临时文件
                        }
                    }
                    StorageType.STORAGE_ALI -> {
                        val tempFile = FileUtils.multipartFileToFile(file)
                        try {
                            val bucket = aliOssProps.bucket
                            val fileUPath = "$type/$filePath"
                            ossClient.putObject(bucket, "handsock/$fileUPath", tempFile)
                            filePath = "https://$bucket.${aliOssProps.endPoint}/handsock/$fileUPath"
                        } finally {
                            tempFile.delete() // 确保上传完成后删除临时文件
                        }
                    }
                    else -> {
                        filePath = "${it.uid}/$time/$fileName"
                        Files.write(uploadDir.resolve(Objects.requireNonNull(fileName)), file.bytes)
                    }
                }

                val fid = UploadManage(HandUtils, IDGenerator).insertUploadFile(uploadModel, it.uid, fileName, filePath, time, type, file.size)
                if (uploadDao.insert(uploadModel) > 0) {
                    ConsoleUtils.printCustomLogs(
                        hash = fid,
                        uid = it.uid,
                        message = filePath,
                        content = "Upload $type",
                        address = clientServiceImpl.getHttpClientIp(request)
                    )
                    return HandUtils.handleResultByCode(HttpStatus.OK, object : HashMap<Any?, Any?>() {
                        init {
                            put("path", filePath)
                        }
                    }, "上传成功")
                } else return HandUtils.handleResultByCode(HttpStatus.INTERNAL_SERVER_ERROR, null, "上传失败")
            }.also {
                return HandUtils.handleResultByCode(HttpStatus.FORBIDDEN, null, "禁止访问")
            }
        } catch (e: Exception) {
            return ResultUtils.printErrorMessage(e)
        }
    }

    private fun createUploadDirectory(type: String, uid: String, time: String): Path {
        val uploadDir = if (type == "avatar") {
            Paths.get("$AVATAR_DIRECTORY$uid/$time/")
        } else if (type == "video") {
            Paths.get("$VIDEOS_DIRECTORY$uid/$time/")
        } else if (type == "image") {
            Paths.get("$IMAGES_DIRECTORY$uid/$time/")
        } else Paths.get("$FILES_DIRECTORY$uid/$time/")
        if (!Files.exists(uploadDir)) Files.createDirectories(uploadDir)
        return uploadDir
    }

    companion object {
        private const val FILES_DIRECTORY = "upload/file/"
        private const val AVATAR_DIRECTORY = "upload/avatar/"
        private const val IMAGES_DIRECTORY = "upload/image/"
        private const val VIDEOS_DIRECTORY = "upload/video/"
    }
}