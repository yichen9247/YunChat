package com.android.yunchat.layout

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.yunchat.activity.UserActivity
import com.android.yunchat.component.SquareAsyncImage
import com.android.yunchat.config.UIConfig
import com.android.yunchat.enmu.StorageTypeEnum
import com.android.yunchat.screen.home.view.HomeViewModel
import com.android.yunchat.service.instance.activityServiceInstance
import com.android.yunchat.state.GlobalState
import com.android.yunchat.utils.HandUtils
import kotlinx.coroutines.launch
import top.chengdongqing.weui.core.ui.components.scanner.rememberScanCodeLauncher
import top.chengdongqing.weui.core.ui.components.toast.rememberToastState

@Composable
fun HomeDrawerLayout(drawerState: DrawerState) {
    DrawerUserBox()
    DrawerMenuList(drawerState)
}

@Composable
private fun DrawerUserBox() {
    val context = LocalContext.current
    val userInfo by GlobalState.userInfo

    Column (
        modifier = Modifier
            .fillMaxWidth()
            .height(175.dp)
            .clickable(
                indication = null,
                onClick = {
                    activityServiceInstance.intentActivityAboutContext(
                        context = context,
                        activity = UserActivity::class.java
                    ) {
                        putLong("uid", userInfo?.uid?:0L)
                    }
                },
                interactionSource = null
            )
            .background(MaterialTheme.colorScheme.secondary),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            SquareAsyncImage(
                size = 65.dp,
                shape = CircleShape,
                model = HandUtils.getStorageFileUrl(
                    type = StorageTypeEnum.AVATAR,
                    path = userInfo?.avatar.toString()
                )
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            fontSize = 20.sp,
            color = Color.White,
            text = userInfo?.nick.toString()
        )
    }
}

@Composable
private fun DrawerMenuList(
    drawerState: DrawerState,
    homeViewModel: HomeViewModel = viewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val toastState = rememberToastState()
    val userInfo by GlobalState.userInfo
    val scanCode = rememberScanCodeLauncher {
        homeViewModel.handleScanResult(
            content = it[0],
            toastState = toastState
        )
    }

    fun handleMenuClick(key: String) {
        when(key) {
            "scan" -> scanCode()
        }
    }

    Spacer(modifier = Modifier.height(5.dp))
    Column {
        UIConfig.homeDrawerMenuList.forEach {
            if ((userInfo?.permission ?: 0) >= it.type) NavigationDrawerItem(
                icon = {
                    Icon(
                        it.icon,
                        contentDescription = null
                    )
                },
                selected = false,
                onClick = {
                    scope.launch {
                        drawerState.close()
                        handleMenuClick(it.key)
                        it.call(context)
                    }
                },
                label = { Text(it.name) }
            )
        }
    }
}