package com.server.handsock.upload.service

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.server.handsock.admin.dao.ServerUserDao
import com.server.handsock.admin.mod.ServerUserModel
import com.server.handsock.admin.service.ServerSystemService
import com.server.handsock.client.dao.ClientUserDao
import com.server.handsock.client.mod.ClientUserModel
import com.server.handsock.common.dao.ChannelDao
import com.server.handsock.common.dao.UploadDao
import com.server.handsock.common.model.ChannelModel
import com.server.handsock.common.model.UploadModel
import com.server.handsock.common.types.UserAuthType
import com.server.handsock.common.utils.ConsoleUtils
import com.server.handsock.common.utils.HandUtils
import com.server.handsock.common.utils.IDGenerator
import com.server.handsock.service.TokenService
import com.server.handsock.upload.man.UploadManage
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*

@Service
class UploadService @Autowired constructor(
    private val uploadDao: UploadDao,
    private val channelDao: ChannelDao,
    private val tokenService: TokenService,
    private val serverUserDao: ServerUserDao,
    private val clientUserDao: ClientUserDao,
    private val serverSystemService: ServerSystemService,
) {
    /**
     * Handles file upload from client.
     *
     * @param headers HTTP headers containing user and group information.
     * @param file    the file to be uploaded.
     * @return a map containing the result of the upload operation.
     */
    @Throws(IOException::class)
    fun clientUploadFile(headers: HttpHeaders, @RequestParam("file") file: MultipartFile, type: String): Map<String, Any> {
        val uidHeader = headers["uid"]
        val gidHeader = headers["gid"]
        val tokenHeader = headers["token"]
        if (!isValidRequest(
            uidHeader = uidHeader,
            gidHeader = gidHeader,
            tokenHeader = tokenHeader
        )) return HandUtils.handleResultByCode(HttpStatus.FORBIDDEN, null, "禁止访问")

        if (!serverSystemService.getSystemKeyStatus("upload") && clientUserDao.selectOne(QueryWrapper<ClientUserModel>().eq("uid", uidHeader?.get(0)!!)).permission != UserAuthType.ADMIN_AUTHENTICATION) {
            return HandUtils.handleResultByCode(HttpStatus.FORBIDDEN, null, "上传权限未开放")
        }

        val time = HandUtils.encodeStringToMD5(HandUtils.formatTimeForString("yyyy-MM-dd-HH-mm-ss"))
        val uploadDir = createUploadDirectory(
            type = type,
            time = time,
            uid = uidHeader!![0]
        )

        try {
            val uploadModel = UploadModel()
            val uid = uidHeader[0].toLong()
            val fileName = file.originalFilename
            val filePath = "$uid/$time/$fileName"
            Files.write(uploadDir.resolve(Objects.requireNonNull(fileName)), file.bytes)
            UploadManage(HandUtils, IDGenerator).insertUploadFile(uploadModel, uid, fileName, filePath, time, type, file.size)
            if (uploadDao.insert(uploadModel) > 0) {
                ConsoleUtils.printInfoLog("upload file $uid $filePath")
                return HandUtils.handleResultByCode(HttpStatus.OK, object : HashMap<Any?, Any?>() {
                    init {
                        put("path", filePath)
                    }
                }, "上传成功")
            } else return HandUtils.handleResultByCode(HttpStatus.INTERNAL_SERVER_ERROR, null, "上传失败")
        } catch (e: IOException) {
            return HandUtils.printErrorLog(e)
        }
    }

    /**
     * Validates the request by checking user and group existence and token validity.
     *
     * @param uidHeader   user ID from headers.
     * @param gidHeader   group ID from headers.
     * @param tokenHeader token from headers.
     * @return true if the request is valid, false otherwise.
     */
    private fun isValidRequest(
        uidHeader: List<String>?,
        gidHeader: List<String>?,
        tokenHeader: List<String>?
    ): Boolean {
        if (uidHeader == null || gidHeader == null || tokenHeader == null) return false
        val user = serverUserDao.selectOne(QueryWrapper<ServerUserModel>().eq("uid", uidHeader[0]))
        val group = channelDao.selectOne(QueryWrapper<ChannelModel>().eq("gid", gidHeader[0]))
        val isTokenValid = tokenService.validUserToken(uidHeader[0].toLong(), tokenHeader[0])
        return user != null && group != null && isTokenValid
    }

    /**
     * Creates the upload directory for the file.
     *
     * @param uid  user ID.
     * @param time time stamp for directory naming.
     * @return the path to the upload directory.
     * @throws IOException if directory creation fails.
     */
    @Throws(IOException::class)
    private fun createUploadDirectory(type: String, uid: String, time: String): Path {
        val uploadDir = if (type == "avatar") {
            Paths.get("$AVATAR_DIRECTORY$uid/$time/")
        } else if (type == "images") {
            Paths.get("$IMAGES_DIRECTORY$uid/$time/")
        } else Paths.get("$FILES_DIRECTORY$uid/$time/")
        if (!Files.exists(uploadDir)) Files.createDirectories(uploadDir)
        return uploadDir
    }

    companion object {
        private const val FILES_DIRECTORY = "upload/files/"
        private const val AVATAR_DIRECTORY = "upload/avatar/"
        private const val IMAGES_DIRECTORY = "upload/images/"
    }
}