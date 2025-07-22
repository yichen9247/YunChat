package com.android.yunchat.screen.frame.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.yunchat.component.SquareAsyncImage
import com.android.yunchat.enmu.StorageTypeEnum
import com.android.yunchat.model.GroupInfoModel
import com.android.yunchat.service.instance.activityServiceInstance
import com.android.yunchat.service.instance.messageServiceInstance
import com.android.yunchat.service.instance.sharedHomeFrameViewModel
import com.android.yunchat.service.instance.userServiceInstance
import com.android.yunchat.utils.HandUtils
import top.chengdongqing.weui.core.ui.components.refreshview.WeRefreshView
import top.chengdongqing.weui.core.ui.components.refreshview.rememberLoadMoreState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeFrameScreen() {
    val loadMoreState = rememberLoadMoreState {}

    LaunchedEffect(Unit) {
        sharedHomeFrameViewModel.getGroupList()
    }

    WeRefreshView(
        modifier = Modifier
            .nestedScroll(loadMoreState.nestedScrollConnection),
        onRefresh = {
            sharedHomeFrameViewModel.getGroupList()
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            HomeChatList()
        }
    }
}

@Composable
private fun HomeChatList() {
    LazyColumn {
        sharedHomeFrameViewModel.groupList.value.forEach { it ->
            item {
                HomeChatListItem(it)
            }
        }
    }
}

@Composable
private fun HomeChatListItem(groupInfo: GroupInfoModel) {
    var msgContent = "暂无更多未读消息"
    val context = LocalContext.current
    if (groupInfo.message.isNotEmpty()) {
        val message = groupInfo.message[0]
        val nick = if (message.uid == userServiceInstance.getUserUid()) "我：" else "其他人："
        val content = messageServiceInstance.getMessageType(
            length = 8,
            type = message.type,
            content = message.content
        )
        msgContent = nick + content
    }

    Spacer(modifier = Modifier.height(1.dp))
    Row(
        modifier = Modifier
            .height(75.dp)
            .fillMaxWidth()
            .clickable(
                onClick = {
                    activityServiceInstance.intentChatActivity(
                        context = context,
                        groupInfo = groupInfo
                    )
                }
            )
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
                    text = msgContent,
                    color = Color(0xFF999999),
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}