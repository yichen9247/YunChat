package com.android.yunchat.layout

import android.Manifest
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.yunchat.component.ArrowRightIcon
import com.android.yunchat.layout.view.UserBindingViewModel
import com.android.yunchat.tencent.TencentCallback
import com.android.yunchat.tencent.controller.handleTencentLogin
import com.android.yunchat.tencent.model.TencentViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun UserBindingLayout(
    onDismiss: () -> Unit,
    tencentViewModel: TencentViewModel = viewModel(),
    userBindingViewModel: UserBindingViewModel = viewModel()
) {
    val context = LocalContext.current
    val bingData = userBindingViewModel.bingData
    val coroutineScope = rememberCoroutineScope()
    val deviceInfoPermissionState = rememberPermissionState(
        Manifest.permission.READ_PHONE_STATE
    )

    TencentCallback({
        coroutineScope.launch {
            userBindingViewModel.fetchUserQQBind(
                data = it,
                onDismiss = onDismiss
            )
        }
    })

    Column(
        modifier = Modifier
            .padding(9.dp, 5.dp)
            .fillMaxWidth()
    ) {
        UserBindBodyItem(
            name = "QQ账号",
            status = bingData.value.qq,
            onClick = {
                if (bingData.value.qq) {
                    userBindingViewModel.fetchUserUnQQBind(onDismiss)
                } else coroutineScope.launch {
                    handleTencentLogin(
                        context = context,
                        tencentViewModel = tencentViewModel,
                        deviceInfoPermissionState = deviceInfoPermissionState
                    )
                }
            }
        )
    }
}

@Composable
private fun UserBindBodyItem(
    name: String,
    status: Boolean,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .height(55.dp)
            .fillMaxWidth()
            .background(
                shape = RoundedCornerShape(6.dp),
                color = MaterialTheme.colorScheme.onBackground
            )
            .padding(15.dp, 0.dp)
            .clickable(
                indication = null,
                onClick = {
                    onClick()
                },
                interactionSource = null
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = name,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.secondary
        )

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                fontSize = 16.sp,
                color = Color.Gray,
                text = if (status) "点击解绑" else "点击绑定",
            )
            Spacer(modifier = Modifier.width(10.dp))
            ArrowRightIcon(16.dp)
        }
    }
}