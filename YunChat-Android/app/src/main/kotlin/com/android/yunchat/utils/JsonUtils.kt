package com.android.yunchat.utils

import com.google.gson.Gson
import java.lang.reflect.Type

/**
 * @name Json工具类
 * @author yichen9247
 */
object JsonUtils {
    val gson = Gson()
    inline fun <reified T> fromJson(json: String): T {
        return gson.fromJson(json, T::class.java)
    }

    fun <T> fromJson(json: String, type: Type): T {
        return gson.fromJson(json, type)
    }
}