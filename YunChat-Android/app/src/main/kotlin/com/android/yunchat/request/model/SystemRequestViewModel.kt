package com.android.yunchat.request.model

import androidx.lifecycle.ViewModel
import com.android.yunchat.model.ResultModel
import com.android.yunchat.model.UpdateInfoModel
import com.android.yunchat.request.repository.impl.SystemRequestRepositoryImpl

class SystemRequestViewModel: ViewModel() {
    private val systemRequestRepository by lazy {
        SystemRequestRepositoryImpl()
    }

    suspend fun fetchCheckUpdate(): ResultModel<UpdateInfoModel>? {
        return systemRequestRepository.fetchCheckUpdate()
    }
}