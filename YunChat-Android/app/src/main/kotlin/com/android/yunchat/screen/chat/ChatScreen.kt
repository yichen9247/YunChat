package com.android.yunchat.screen.chat

import android.annotation.SuppressLint
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.ExitToApp
import androidx.compose.material.icons.outlined.Notifications
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.yunchat.component.ChatMessageItem
import com.android.yunchat.component.LoadingFrame
import com.android.yunchat.component.SubPageToolbar
import com.android.yunchat.config.AppConfig
import com.android.yunchat.config.UIConfig
import com.android.yunchat.screen.chat.view.ChatViewModel
import com.android.yunchat.service.instance.activityServiceInstance
import com.android.yunchat.service.instance.dialogServiceInstance
import com.xuexiang.xutil.XUtil
import top.chengdongqing.weui.core.data.model.VisualMediaType
import top.chengdongqing.weui.core.ui.components.dialog.rememberDialogState
import top.chengdongqing.weui.core.ui.components.mediapicker.rememberPickMediasLauncher
import top.chengdongqing.weui.core.ui.components.toast.rememberToastState
import top.chengdongqing.weui.core.utils.RequestMediaPermission
import top.chengdongqing.weui.core.utils.showToast

@Composable
fun ChatScreen(
    intent: Intent,
    chatViewModel: ChatViewModel = viewModel()
) {
    val context = LocalContext.current
    val dialogState = rememberDialogState()
    val groupName = intent.getStringExtra("name")
    val groupNumber = intent.getIntExtra("gid", 0)
    val groupMember = intent.getIntExtra("member", 0)
    val groupNotice = intent.getStringExtra("notice")
    chatViewModel.currentGroupId.intValue = groupNumber

    LaunchedEffect(Unit) {
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
                name = "${groupName}（$groupMember）",
                action = {
                    IconButton(onClick = {
                        dialogState.show(
                            onCancel = null,
                            title = "群聊公告",
                            okText = "我已知晓",
                            content = groupNotice
                        )
                    }) {
                        Icon(
                            contentDescription = "群聊公告",
                            imageVector = Icons.Outlined.Notifications
                        )
                    }
                    IconButton(onClick = {
                        dialogServiceInstance.openExitGroupDialog(
                            gid = groupNumber,
                            context = context,
                            name = groupName.toString()
                        )
                    }) {
                        Icon(
                            contentDescription = "退出群聊",
                            imageVector = Icons.Outlined.ExitToApp
                        )
                    }
                }
            )
        },
        bottomBar = { ChatScreenFoot(chatViewModel) }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .padding(innerPadding),
            color = MaterialTheme.colorScheme.background
        ) {
            RequestMediaPermission(onRevoked = {
                activityServiceInstance.intentActivityBack(context)
            }) {
                ChatScreenBody(chatViewModel)
            }
        }
    }
}

@Composable
private fun ChatScreenBody(chatViewModel: ChatViewModel) {
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
        LazyColumn(
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
                    chatViewModel = chatViewModel,
                    message = reversedMessages[index]
                )
            }
            item { Spacer(Modifier.height(10.dp)) }
        }
    } else LoadingFrame()
}

@Composable
@SuppressLint("UnrememberedMutableState")
private fun ChatScreenFoot(chatViewModel: ChatViewModel) {
    val inputValue by chatViewModel.inputValue
    val isMoreOpen by chatViewModel.isMoreOpen
    val color = MaterialTheme.colorScheme.secondary
    val moreButtonIcon = if (isMoreOpen) Icons.Outlined.Close else Icons.Outlined.Add

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
                ChatScreenFootButton(
                    icon = moreButtonIcon,
                    onClick = { chatViewModel.isMoreOpen.value = !isMoreOpen }
                )
                Spacer(modifier = Modifier.width(15.dp))
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
                ChatScreenFootButton(
                    icon = Icons.Outlined.Send,
                    onClick = {
                        chatViewModel.sendTextMessage()
                    }
                )
            }
            AnimatedVisibility(
                visible = isMoreOpen,
                exit = shrinkVertically(tween(AppConfig.animationSpec.toInt())),
                enter = expandVertically(tween(AppConfig.animationSpec.toInt()))
            ) { ChatScreenFunction(chatViewModel) }
        }
    }
}

@Composable
private fun ChatScreenFootButton(icon: ImageVector, onClick: () -> Unit) {
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

@Composable
private fun ChatScreenFunction(chatViewModel: ChatViewModel) {
    val toastState = rememberToastState()
    val uploadImage = rememberPickMediasLauncher {
        chatViewModel.handleSelectImage(it, toastState)
    }
    val uploadFile = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        chatViewModel.handleSelectFile(it, toastState)
    }
    val uploadVideo = rememberPickMediasLauncher {
        chatViewModel.handleSelectVideo(it, toastState)
    }

    Row(
        modifier = Modifier
            .height(85.dp)
            .fillMaxWidth()
            .padding(horizontal = 15.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        UIConfig.chatFunctionList.forEach { (name, icon) ->
            ChatScreenFunctionBox(name, icon) {
                when(name) {
                    "发送图片" -> uploadImage(VisualMediaType.IMAGE, 1)
                    "发送视频" ->  uploadVideo(VisualMediaType.VIDEO, 1)
                    "发送文件" -> uploadFile.launch(Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                        type = "*/*" // 允许选择任意类型的文件
                        addCategory(Intent.CATEGORY_OPENABLE)
                    })
                    "发送语音" -> XUtil.getContext().showToast("暂不支持此功能")
                }
            }
        }
    }
}

@Composable
private fun ChatScreenFunctionBox(
    name: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    val color = MaterialTheme.colorScheme.secondary
    Column(
        modifier = Modifier
            .size(70.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .border(
                width = 1.dp,
                color = color,
                shape = RoundedCornerShape(8.dp)
            ),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            icon, tint = color,
            contentDescription = null,
            modifier = Modifier.size(30.dp)
        )
        Spacer(Modifier.height(5.dp))
        Text(name, fontSize = 12.sp, color = color)
    }
}