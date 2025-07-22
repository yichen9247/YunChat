package com.android.yunchat.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.InsertDriveFile
import androidx.compose.material.icons.outlined.VideoCameraFront
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.yunchat.R
import com.android.yunchat.activity.UserActivity
import com.android.yunchat.config.UIConfig
import com.android.yunchat.enmu.StorageTypeEnum
import com.android.yunchat.model.GroupInfoModel
import com.android.yunchat.model.MessageColors
import com.android.yunchat.model.MessageInfoModel
import com.android.yunchat.model.UserInfoModel
import com.android.yunchat.request.model.GroupRequestViewModel
import com.android.yunchat.screen.chat.view.ChatViewModel
import com.android.yunchat.service.instance.activityServiceInstance
import com.android.yunchat.service.instance.fileServiceInstance
import com.android.yunchat.service.instance.userServiceInstance
import com.android.yunchat.state.GlobalState
import com.android.yunchat.types.UserAuthType
import com.android.yunchat.utils.HandUtils
import com.android.yunchat.utils.StringUtils
import com.google.gson.Gson
import kotlinx.coroutines.launch
import top.chengdongqing.weui.core.data.model.MediaType
import top.chengdongqing.weui.core.ui.components.toast.rememberToastState

@Composable
fun ChatMessageItem(
    showInfo: Boolean = true,
    message: MessageInfoModel,
    chatViewModel: ChatViewModel
) {
    val userList by lazy { chatViewModel.userList }
    val user = remember(userList) {
        userList.find { it.uid == message.uid } ?: UserInfoModel(
            nick = "",
            status = 0,
            avatar = "",
            regTime = "",
            username = "",
            uid = message.uid,
            permission = UserAuthType.USER_AUTHENTICATION
        )
    }
    if (message.type in UIConfig.supportMessageType) {
        Spacer(Modifier.height(10.dp))
        if (userServiceInstance.getUserUid() == message.uid) {
            MessageLayout(
                user = user,
                isSelf = true,
                message = message,
                showInfo = showInfo
            )
        } else MessageLayout(
            user = user,
            isSelf = false,
            message = message,
            showInfo = showInfo
        )
        Spacer(Modifier.height(10.dp))
    }
}

@Composable
private fun messageColors(isSelf: Boolean): MessageColors {
    return if (isSelf) {
        MessageColors(
            content = MaterialTheme.colorScheme.onBackground,
            background = MaterialTheme.colorScheme.secondary
        )
    } else MessageColors(
        content = Color.Gray,
        background = MaterialTheme.colorScheme.onBackground
    )
}

@Composable
private fun MessageLayout(
    isSelf: Boolean,
    showInfo: Boolean,
    user: UserInfoModel,
    message: MessageInfoModel
) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        if (isSelf) {
            Row (
                modifier = Modifier
                    .weight(1f),
                horizontalArrangement = Arrangement.End
            ) {
                MessageContentSection(
                    user = user,
                    isSelf = true,
                    message = message,
                    showInfo = showInfo
                )
            }
            if (showInfo) {
                Spacer(Modifier.width(12.dp))
                UserAvatar(user)
            }
        } else {
            if (showInfo) {
                UserAvatar(user)
                Spacer(Modifier.width(12.dp))
            }
            Row (
                modifier = Modifier
                    .weight(1f),
                horizontalArrangement = Arrangement.Start
            ) {
                MessageContentSection(
                    user = user,
                    isSelf = false,
                    message = message,
                    showInfo = showInfo
                )
            }
        }
    }
}

@Composable
private fun MessageContentSection(
    isSelf: Boolean,
    showInfo: Boolean,
    user: UserInfoModel,
    message: MessageInfoModel
) {
    Column(
        modifier = Modifier
            .fillMaxWidth(0.9f),
        horizontalAlignment = if (isSelf) Alignment.End else Alignment.Start
    ) {
        if (showInfo) {
            UserInfoRow(isSelf, user, message)
            Spacer(modifier = Modifier.height(10.dp))
        }
        MessageBubble(user, message)
    }
}

@Composable
private fun UserInfoRow(
    isSelf: Boolean,
    user: UserInfoModel,
    message: MessageInfoModel
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = if (isSelf) Arrangement.End else Arrangement.Start
    ) {
        if (isSelf) {
            TimeText(message.time)
            Spacer(modifier = Modifier.width(10.dp))
            NicknameText(user.nick)
        } else {
            NicknameText(user.nick)
            Spacer(modifier = Modifier.width(10.dp))
            TimeText(message.time)
        }
    }
}

@Composable
private fun UserAvatar(user: UserInfoModel) {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .clickable(
                onClick = {
                    activityServiceInstance.intentActivityAboutContext(
                        context = context,
                        activity = UserActivity::class.java
                    ) {
                        putLong("uid", user.uid)
                    }
                }
            )
    ) {
        SquareAsyncImage(
            size = 45.dp,
            shape = CircleShape,
            model = HandUtils.getStorageFileUrl(
                path = user.avatar,
                type = StorageTypeEnum.AVATAR
            )
        )
    }
}

@Composable
private fun NicknameText(nick: String?) {
    Text(
        fontSize = 15.sp,
        text = nick.toString(),
        color = MaterialTheme.colorScheme.secondary
    )
}

@Composable
private fun TimeText(time: String) {
    Text(
        fontSize = 15.sp,
        color = Color.Gray,
        text = StringUtils.getMessageTime(time)
    )
}

@Composable
private fun MessageBubble(
    user: UserInfoModel,
    message: MessageInfoModel
) {
    val isSelf = user.uid == GlobalState.userInfo.value?.uid
    val colors = messageColors(isSelf)

    Box(
        modifier = Modifier
            .background(
                color = colors.background,
                shape = RoundedCornerShape(6.dp)
            )
    ) {
        when (message.type) {
            "text" -> TextMessage(message.content, colors)
            "image" -> ImageMessage(message.content)
            "file" -> FileMessage(message.content, colors)
            "video" -> VideoMessage(message.content, colors)
            "invite" -> InviteMessage(message.content, colors)
        }
    }
}

@Composable
private fun TextMessage(
    content: String,
    colors: MessageColors
) {
    SelectionContainer {
        Text(
            fontSize = 14.sp,
            lineHeight = 22.sp,
            color = colors.content,
            modifier = Modifier.padding(15.dp),
            text = StringUtils.getHtmlContent(content)
        )
    }
}

@Composable
private fun ImageMessage(content: String) {
    SquareAsyncImage(
        save = true,
        preview = true,
        autoSize = true,
        placeholder = false,
        contentScale = ContentScale.FillWidth,
        model = HandUtils.getStorageFileUrl(
            path = content,
            type = StorageTypeEnum.IMAGE
        ),
        srcUrl = HandUtils.getStorageFileUrl(
            path = content,
            type = StorageTypeEnum.IMAGE
        )
    )
}

@Composable
private fun MediaMessage(
    label: String,
    icon: ImageVector,
    description: String,
    colors: MessageColors,
    onClick: () -> Unit
) {

    Box(
        modifier = Modifier
            .height(65.dp)
            .fillMaxWidth()
            .background(
                color = colors.background,
                shape = RoundedCornerShape(6.dp)
            )
            .clip(RoundedCornerShape(6.dp)),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .clickable { onClick() },
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon, tint = colors.content,
                modifier = Modifier.size(28.dp),
                contentDescription = description
            )
            Spacer(Modifier.width(10.dp))
            Text(
                text = label,
                fontSize = 20.sp,
                color = colors.content
            )
        }
    }
}

@Composable
private fun VideoMessage(
    content: String,
    colors: MessageColors
) {
    val context = LocalContext.current
    val toastState = rememberToastState()
    val coroutineScope = rememberCoroutineScope()

    MediaMessage(
        colors = colors,
        label = "发送了视频",
        description = "视频图标",
        icon = Icons.Outlined.VideoCameraFront
    ) {
        coroutineScope.launch {
            fileServiceInstance.openMediaPreview(
                content = content,
                context = context,
                toastState = toastState,
                mediaType = MediaType.VIDEO
            )
        }
    }
}

@Composable
private fun FileMessage(
    content: String,
    colors: MessageColors
) {
    val toastState = rememberToastState()
    val coroutineScope = rememberCoroutineScope()

    MediaMessage(
        colors = colors,
        label = "发送了文件",
        description = "文件图标",
        icon = Icons.Outlined.InsertDriveFile
    ) {
        coroutineScope.launch {
            fileServiceInstance.openSystemFilePreview(
                toastState = toastState,
                content = HandUtils.getStorageFileUrl(
                    path = content,
                    type = StorageTypeEnum.FILE
                )
            )
        }
    }
}

@Composable
private fun InviteMessage(
    content: String,
    colors: MessageColors,
    groupRequestViewModel: GroupRequestViewModel = viewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    val group = Gson().fromJson(content, GroupInfoModel::class.java)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = colors.background,
                shape = RoundedCornerShape(6.dp)
            )
            .clickable(
                onClick = {
                    coroutineScope.launch {
                        groupRequestViewModel.fetchJoinGroup(group.gid)
                    }
                }
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp, 12.dp)
        ) {
            Text(
                fontSize = 16.sp,
                text = "邀请你加入群聊",
                color = colors.content
            )
            Spacer(Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .height(45.dp)
            ) {
                Row (
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                ) {
                    Text(
                        maxLines = 2,
                        fontSize = 13.sp,
                        lineHeight = 26.sp,
                        color = colors.content,
                        overflow = TextOverflow.Ellipsis,
                        text = stringResource(R.string.invite_message_tip, group.name)
                    )
                }
                Spacer(Modifier.width(5.dp))
                SquareAsyncImage(
                    size = 45.dp,
                    model = HandUtils.getStorageFileUrl(
                        path = group.avatar,
                        type = StorageTypeEnum.AVATAR
                    ),
                    shape = RoundedCornerShape(6.dp)
                )
            }
        }
    }
}