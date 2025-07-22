package com.android.yunchat.utils

import com.android.yunchat.config.AppConfig
import com.android.yunchat.enmu.StorageTypeEnum

/**
 * @name YunChat工具类
 * @author yichen9247
 */
object HandUtils {
    /**
     * @name 获取文件下载地址
     * @param path 文件路径
     * @param type 文件类型
     * @return String
     */
    fun getStorageFileUrl(path: String, type: StorageTypeEnum): String {
        val trimmedPath = path.trim()
        if (!URL_PATTERN.matches(trimmedPath)) {
            return when (type) {
                StorageTypeEnum.FILE -> "${AppConfig.serverUrl}/api/download/file/$trimmedPath"
                StorageTypeEnum.IMAGE -> "${AppConfig.serverUrl}/api/download/image/$trimmedPath"
                StorageTypeEnum.VIDEO -> "${AppConfig.serverUrl}/api/download/video/$trimmedPath"
                StorageTypeEnum.AVATAR -> "${AppConfig.serverUrl}/api/download/avatar/$trimmedPath"
                else -> trimmedPath
            }
        }
        return trimmedPath
    }
    private val URL_PATTERN = Regex("^(https?://|ftp://|mailto:|tel:)?([a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}(/\\S*)?$")
}