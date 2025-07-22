package com.android.yunchat.utils

import android.util.Log

object ConsoleUtils {
    fun printInfoLog(content: Any) {
        Log.d("YunChat-LOG", content.toString())
    }
}