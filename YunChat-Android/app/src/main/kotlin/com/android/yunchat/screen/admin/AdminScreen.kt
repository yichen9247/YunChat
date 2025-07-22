package com.android.yunchat.screen.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.yunchat.component.SubPageToolbar
import com.android.yunchat.screen.admin.view.AdminViewModel
import top.chengdongqing.weui.core.DefaultLoadPage
import top.chengdongqing.weui.core.ui.components.cardlist.WeCardListItem

@Composable
fun AdminScreen(
    adminViewModel: AdminViewModel = viewModel()
) {
    DisposableEffect(Unit) {
        onDispose {
            adminViewModel.dashBoardData.value = null
        }
    }

    Scaffold(
        topBar = {
            SubPageToolbar("管理后台")
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = MaterialTheme.colorScheme.background
        ) {
            when {
                adminViewModel.dashBoardData.value == null -> DefaultLoadPage()
                else -> AdminScreenBody(adminViewModel)
            }
        }
    }
}

@Composable
private fun AdminScreenBody(adminViewModel: AdminViewModel) {
    LazyColumn (
        modifier = Modifier
            .padding(15.dp, 0.dp)
    ) {
        item { Spacer(modifier = Modifier.height(15.dp)) }
        item { AdminScreenBodyDash(adminViewModel) }
        item { Spacer(modifier = Modifier.height(15.dp)) }
        item { AdminDashSystemInfo(adminViewModel) }
        item { Spacer(modifier = Modifier.height(15.dp)) }
        item { AdminDashFooter() }
        item { Spacer(modifier = Modifier.height(15.dp)) }
    }
}

@Composable
private fun AdminScreenBodyDash(
    adminViewModel: AdminViewModel
) {
    val dashBoardData by lazy {
        adminViewModel.dashBoardData.value
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Box(
            modifier = Modifier.weight(1f)
        ) {
            AdminDashCardItem(
                name = "注册用户",
                value = dashBoardData?.userTotal.toString()
            )
        }
        Spacer(modifier = Modifier.width(15.dp))
        Box(
            modifier = Modifier.weight(1f)
        ) {
            AdminDashCardItem(
                name = "今日注册",
                value = dashBoardData?.todayRegUser.toString()
            )
        }
    }
    Spacer(modifier = Modifier.height(15.dp))
    Row(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Box(
            modifier = Modifier.weight(1f)
        ) {
            AdminDashCardItem(
                name = "总频道数",
                value = dashBoardData?.chanTotal.toString()
            )
        }
        Spacer(modifier = Modifier.width(15.dp))
        Box(
            modifier = Modifier.weight(1f)
        ) {
            AdminDashCardItem(
                name = "今日消息",
                value = dashBoardData?.todayChatTotal.toString()
            )
        }
    }
}

@Composable
private fun AdminDashCardItem(
    name: String,
    value: String
) {
    Box(
        modifier = Modifier
            .height(110.dp)
            .fillMaxWidth()
            .background(
                shape = RoundedCornerShape(6.dp),
                color = MaterialTheme.colorScheme.onBackground
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(15.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = name,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.secondary
                )

                Text(
                    text = value,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun AdminDashSystemInfo(adminViewModel: AdminViewModel) {
    val systemOsInfo by lazy {
        adminViewModel.dashBoardData.value?.systemOsInfo
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                shape = RoundedCornerShape(6.dp),
                color = MaterialTheme.colorScheme.onBackground
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp)
        ) {
            Text(
                text = "系统信息",
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.secondary
            )
            Spacer(modifier = Modifier.height(5.dp))
            WeCardListItem("主机名称", systemOsInfo?.hostName)
            WeCardListItem("系统语言", systemOsInfo?.locale)
            WeCardListItem("系统架构", systemOsInfo?.osArch)
            WeCardListItem("主机地址", systemOsInfo?.hostAddress)
            WeCardListItem("操作系统", systemOsInfo?.osInfo)
            WeCardListItem("系统时区", systemOsInfo?.timeZoneId)
            WeCardListItem("系统内存", systemOsInfo?.memoryUsageInfo)
            WeCardListItem("核心数量", systemOsInfo?.logicalCount.toString())
            WeCardListItem("程序版本", systemOsInfo?.appVersion)
            WeCardListItem("运行时长", systemOsInfo?.systemUptime)
        }
    }
}

@Composable
private fun AdminDashFooter() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                shape = RoundedCornerShape(6.dp),
                color = MaterialTheme.colorScheme.onBackground
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            fontSize = 14.sp,
            modifier = Modifier
                .padding(0.dp, 15.dp),
            text = "更多后台功能请前往网页端查看",
            color = MaterialTheme.colorScheme.onSecondary
        )
    }
}