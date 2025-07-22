package com.android.yunchat.screen.search

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.yunchat.component.SquareAsyncImage
import com.android.yunchat.component.SubPageToolbar
import com.android.yunchat.enmu.StorageTypeEnum
import com.android.yunchat.model.GroupInfoModel
import com.android.yunchat.request.model.GroupRequestViewModel
import com.android.yunchat.screen.search.view.SearchViewModel
import com.android.yunchat.service.instance.sharedHomeFrameViewModel
import com.android.yunchat.utils.HandUtils
import kotlinx.coroutines.launch

@Composable
fun SearchScreen(
    intent: Intent,
    searchViewModel: SearchViewModel = viewModel()
) {
    DisposableEffect(Unit) {
        onDispose {
            searchViewModel.groupList.value = emptyList()
        }
    }

    LaunchedEffect(Unit) {
        searchViewModel.searchGroupList(intent.getStringExtra("value").toString())
    }

    Scaffold(
        topBar = {
            SubPageToolbar("搜索结果")
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = MaterialTheme.colorScheme.background
        ) {
            SearchGroupList(searchViewModel)
        }
    }
}

@Composable
private fun SearchGroupList(searchViewModel: SearchViewModel) {
    val groupList = searchViewModel.groupList

    if (groupList.value.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "暂无搜索结果",
                color = Color.Gray
            )
        }
    } else LazyColumn {
        groupList.value.forEach { it ->
            item {
                SearchGroupListItem(it)
            }
        }
    }
}

@Composable
private fun SearchGroupListItem(
    groupInfo: GroupInfoModel,
    groupRequestViewModel: GroupRequestViewModel = viewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    val isJoined = sharedHomeFrameViewModel.groupList.value.any {
        it.gid == groupInfo.gid
    }

    Spacer(modifier = Modifier.height(1.dp))
    Row(
        modifier = Modifier
            .height(75.dp)
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.onBackground)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(15.dp, 0.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SquareAsyncImage(
                size = 50.dp,
                shape = RoundedCornerShape(6.dp),
                model = HandUtils.getStorageFileUrl(
                    path = groupInfo.avatar,
                    type = StorageTypeEnum.AVATAR
                )
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    fontSize = 18.sp,
                    text = groupInfo.name,
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    maxLines = 1,
                    fontSize = 16.sp,
                    text = "群号：${groupInfo.gid}",
                    color = Color(0xFF999999),
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            TextButton(
                enabled = !isJoined,
                onClick = {
                    coroutineScope.launch {
                        groupRequestViewModel.fetchJoinGroup(groupInfo.gid)
                    }
                }
            ) {
                Text(if (isJoined) "已加入" else "加入群聊")
            }
        }
    }
}