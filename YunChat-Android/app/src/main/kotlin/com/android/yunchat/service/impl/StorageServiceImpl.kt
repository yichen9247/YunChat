package com.android.yunchat.service.impl

import com.android.yunchat.service.StorageService
import com.tencent.mmkv.MMKV

/**
 * @name 存储服务实现类
 * @author yichen9247
 */
class StorageServiceImpl: StorageService {
    /**
     * @name 获取整型
     * @param key 键
     * @return 值
     */
    override fun getIntData(key: String): Int? {
        return mmkv.decodeInt(key)
    }

    /**
     * @name 获取长整型
     * @param key 键
     * @return 值
     */
    override fun getLongData(key: String): Long? {
        return mmkv.decodeLong(key)
    }

    /**
     * @name 获取字符串
     * @param key 键
     * @return 值
     */
    override fun getStringData(key: String): String? {
        return mmkv.decodeString(key)
    }

    /**
     * @name 获取布尔值
     * @param key 键
     * @return 值
     */
    override fun getBooleanData(key: String): Boolean? {
        return mmkv.decodeBool(key)
    }

    // Tencent MMKV实例
    val mmkv by lazy { MMKV.defaultMMKV() }
}