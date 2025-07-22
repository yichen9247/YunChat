package com.android.yunchat.service

interface RouteService {
    fun navigationBack()
    fun navigationTo(
        route: String,
        allowRepeat: Boolean
    )
}