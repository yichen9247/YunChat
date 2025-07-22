package com.android.yunchat.service

interface StorageService {
    fun getIntData(key: String): Int?
    fun getLongData(key: String): Long?
    fun getStringData(key: String): String?
    fun getBooleanData(key: String): Boolean?
}