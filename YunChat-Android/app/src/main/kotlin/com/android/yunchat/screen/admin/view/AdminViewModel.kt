package com.android.yunchat.screen.admin.view

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.yunchat.model.DashboardModel
import com.android.yunchat.request.model.AdminRequestViewModel
import com.android.yunchat.service.instance.requestServiceInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * @name 后台Viewmodel
 * @author yichen9247
 */
class AdminViewModel: ViewModel() {
    val dashBoardData = mutableStateOf<DashboardModel?>(null)

    init {
        getDashBoardData()
    }

    /**
     * @name 获取后台数据
     */
     private fun getDashBoardData() {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                AdminRequestViewModel().fetchDashboardData()
            }
            result?.let { response ->
                requestServiceInstance.checkResponseResult(
                    response = response,
                    message = "获取后台数据失败",
                    onSuccess = {
                        dashBoardData.value = response.data
                    }
                )
            }
        }
    }
}