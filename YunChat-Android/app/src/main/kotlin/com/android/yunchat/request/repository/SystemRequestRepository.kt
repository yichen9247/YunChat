package com.android.yunchat.request.repository

import com.android.yunchat.model.ResultModel
import com.android.yunchat.model.UpdateInfoModel

interface SystemRequestRepository {
    suspend fun fetchCheckUpdate(): ResultModel<UpdateInfoModel>?
}