package com.android.yunchat.screen.agent

import android.annotation.SuppressLint
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Restore
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.yunchat.component.ChatMessageItem
import com.android.yunchat.component.LoadingFrame
import com.android.yunchat.component.SubPageToolbar
import com.android.yunchat.config.UIConfig
import com.android.yunchat.screen.chat.view.ChatViewModel
import com.android.yunchat.service.instance.clientServiceInstance

@Composable
fun AgentScreen(
    chatViewModel: ChatViewModel = viewModel()
) {
    LaunchedEffect(Unit) {
        chatViewModel.isAgent.value = true
        chatViewModel.bindServiceIfNeeded()
    }

    DisposableEffect(Unit) {
        onDispose {
            chatViewModel.stopSocketService()
        }
    }

    Scaffold(
        topBar = {
            SubPageToolbar(
                name = "智能聊天",
                action = {
                    IconButton(onClick = {
                        clientServiceInstance.resetAiMessage(chatViewModel)
                    }) {
                        Icon(
                            contentDescription = "重置记录",
                            imageVector = Icons.Outlined.Restore
                        )
                    }
                }
            )
        },
        bottomBar = { AgentScreenFoot(chatViewModel) }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .padding(innerPadding),
            color = MaterialTheme.colorScheme.background
        ) {
            AgentScreenBody(chatViewModel)
        }
    }
}

@Composable
private fun AgentScreenBody(chatViewModel: ChatViewModel) {
    val listState = rememberLazyListState()
    val messageList = chatViewModel.messageList
    val reversedMessages by remember(messageList) {
        derivedStateOf { messageList.reversed() }
    } // 反转消息列表

    // 列表更新时滚动到底部
    LaunchedEffect(reversedMessages.size) {
        if (reversedMessages.isNotEmpty()) {
            listState.animateScrollToItem(0)
        }
    }

    // 初始加载时滚动到底部
    LaunchedEffect(Unit) {
        if (reversedMessages.isNotEmpty()) {
            listState.scrollToItem(0)
        }
    }

    if (chatViewModel.connection.value) {
        if (reversedMessages.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "暂无更多记录",
                    color = Color.Gray
                )
            }
        } else LazyColumn(
            modifier = Modifier
                .padding(15.dp, 0.dp),
            state = listState,
            reverseLayout = true
        ) {
            item { Spacer(modifier = Modifier.height(10.dp)) }
            items(
                count = reversedMessages.size,
                key = { index -> reversedMessages[index].sid }
            ) { index ->
                ChatMessageItem(
                    showInfo = false,
                    chatViewModel = chatViewModel,
                    message = reversedMessages[index]
                )
            }
            item { Spacer(modifier = Modifier.height(10.dp)) }
        }
    } else LoadingFrame()
}

@Composable
@SuppressLint("UnrememberedMutableState")
private fun AgentScreenFoot(chatViewModel: ChatViewModel) {
    val inputValue by chatViewModel.inputValue
    val color = MaterialTheme.colorScheme.secondary

    Surface(
        color = Color.White,
        modifier = Modifier
            .fillMaxWidth()
            .then(UIConfig.appBarModifier)
            .windowInsetsPadding(WindowInsets.ime)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize()
        ) {
            Row(
                modifier = Modifier
                    .height(75.dp)
                    .padding(15.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(45.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .border(
                            width = 1.dp,
                            color = color,
                            shape = RoundedCornerShape(8.dp)
                        ),
                ) {
                    BasicTextField(
                        maxLines = 1,
                        textStyle = TextStyle(
                            color = color,
                            fontSize = 16.sp
                        ),
                        value = inputValue,
                        onValueChange = { chatViewModel.inputValue.value = it },
                        decorationBox = { innerTextField ->
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                if (inputValue.isEmpty()) Text(
                                    color = color,
                                    fontSize = 16.sp,
                                    text = "请在此输入消息..."
                                )
                                innerTextField()
                            }
                        },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 12.dp)
                            .onFocusChanged { chatViewModel.isMoreOpen.value = false },
                        cursorBrush = SolidColor(color)
                    )
                }
                Spacer(modifier = Modifier.width(15.dp))
                AgentScreenFootButton(
                    icon = Icons.Outlined.Send,
                    onClick = {
                        chatViewModel.sendAgentMessage()
                    }
                )
            }
        }
    }
}

@Composable
private fun AgentScreenFootButton(icon: ImageVector, onClick: () -> Unit) {
    val color = MaterialTheme.colorScheme.secondary
    Box(
        modifier = Modifier
            .size(45.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .border(
                width = 1.dp,
                color = color,
                shape = RoundedCornerShape(8.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, tint = color, contentDescription = null)
    }
}