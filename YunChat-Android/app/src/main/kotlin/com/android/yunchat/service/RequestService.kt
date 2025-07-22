package com.android.yunchat.service

import com.android.yunchat.model.ResultModel

interface RequestService {
    fun handleRequestError(error: Exception)
    fun checkResponseResult(
        message: String,
        onSuccess: () -> Unit,
        response: ResultModel<*>
    )
}