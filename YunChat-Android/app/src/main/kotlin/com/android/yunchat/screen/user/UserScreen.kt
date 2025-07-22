package com.android.yunchat.screen.user

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.yunchat.R
import com.android.yunchat.component.SquareAsyncImage
import com.android.yunchat.component.SubPageToolbar
import com.android.yunchat.enmu.StorageTypeEnum
import com.android.yunchat.screen.user.view.UserViewModel
import com.android.yunchat.service.instance.dialogServiceInstance
import com.android.yunchat.service.instance.userServiceInstance
import com.android.yunchat.state.GlobalState
import com.android.yunchat.types.UserAuthType
import com.android.yunchat.utils.HandUtils
import top.chengdongqing.weui.core.DefaultLoadPage
import top.chengdongqing.weui.core.data.model.VisualMediaType
import top.chengdongqing.weui.core.ui.components.cardlist.WeCardListItem
import top.chengdongqing.weui.core.ui.components.mediapicker.rememberPickMediasLauncher
import top.chengdongqing.weui.core.ui.components.toast.rememberToastState

@Composable
fun UserScreen(
    intent: Intent,
    userViewModel: UserViewModel = viewModel()
) {
    LaunchedEffect(Unit) {
        userViewModel.getUserInfo(
            intent.getLongExtra("uid", 0)
        )
    }

    DisposableEffect(Unit) {
        onDispose {
            userViewModel.userInfo.value = null
        }
    }

    Scaffold(
        topBar = {
            SubPageToolbar("用户主页")
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = MaterialTheme.colorScheme.background
        ) {
            when {
                userViewModel.userInfo.value == null -> DefaultLoadPage()
                else -> UserActivityBody(userViewModel)
            }
        }
    }
}

@Composable
private fun UserActivityBody(userViewModel: UserViewModel) {
    LazyColumn(
        modifier = Modifier
            .padding(15.dp, 0.dp)
    ) {
        item { Spacer(modifier = Modifier.height(15.dp)) }
        item { UserPhotoBox(userViewModel) }
        item { Spacer(modifier = Modifier.height(15.dp)) }
        item { UserInfoDetail(userViewModel)  }
        item { Spacer(modifier = Modifier.height(15.dp)) }
        if (userViewModel.userInfo.value?.uid == userServiceInstance.getUserUid()) {
            item { UserAction(userViewModel) }
            item { Spacer(modifier = Modifier.height(15.dp)) }
        }
    }
}

@Composable
private fun UserPhotoBox(userViewModel: UserViewModel) {
    val user by userViewModel.userInfo
    val toastState = rememberToastState()
    val isAdmin = user?.permission == UserAuthType.ADMIN_AUTHENTICATION // 是否管理员
    val isRobot = user?.permission == UserAuthType.ROBOT_AUTHENTICATION // 是否机器人

    val uploadAvatar = rememberPickMediasLauncher {
        userViewModel.handleSelectAvatar(it, toastState)
    }

    Box (
        modifier = Modifier
            .height(115.dp)
            .fillMaxWidth()
            .background(
                shape = RoundedCornerShape(6.dp),
                color = MaterialTheme.colorScheme.onBackground
            )
            .clip(RoundedCornerShape(6.dp))
    ) {
        Image(
            modifier = Modifier
                .fillMaxSize(),
            contentDescription = "用户背景",
            contentScale = ContentScale.FillWidth,
            painter = painterResource(R.drawable.user_bg)
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .absoluteOffset(x = 0.dp, y = 0.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp, 0.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .border(
                            width = 5.dp,
                            color = Color(0XFFFFFFFF),
                            shape = RoundedCornerShape(6.dp)
                        )
                        .size(75.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clickable(
                                onClick = {
                                    if (user?.uid == userServiceInstance.getUserUid()) {
                                        uploadAvatar(VisualMediaType.IMAGE, 1)
                                    }
                                }
                            )
                    ) {
                        SquareAsyncImage(
                            size = 75.dp,
                            model = HandUtils.getStorageFileUrl(
                                type = StorageTypeEnum.AVATAR,
                                path = user?.avatar.toString()
                            )
                        )
                    }
                    if (isAdmin || isRobot)
                        Box (
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(6.dp))
                                .absoluteOffset(x = 0.dp, y = 0.dp),
                            contentAlignment = Alignment.BottomEnd
                        ) {
                            Box (
                                modifier = Modifier
                                    .height(28.dp)
                                    .fillMaxWidth()
                                    .padding(0.dp, 0.dp, 0.dp, 5.dp)
                                    .background(color = if (isAdmin) Color(5, 160, 150, 186) else Color(121, 187, 255, 186)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    fontSize = 12.sp,
                                    color = Color(0XFFFFFFFF),
                                    text = if (isAdmin) "管理员" else "机器人"
                                )
                            }
                        }
                }

                Column(
                    modifier = Modifier
                        .height(75.dp)
                        .padding(15.dp, 0.dp, 0.dp, 0.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        fontSize = 18.sp,
                        color = Color(0XFFFFFFFF),
                        text = user?.nick.toString()
                    )
                    Text(
                        fontSize = 14.sp,
                        color = Color(0XFFFFFFFF),
                        text = "UID：" + user?.uid.toString()
                    )

                    Text(
                        fontSize = 14.sp,
                        color = Color(0XFFFFFFFF),
                        text = "于 ${user?.regTime} 注册"
                    )
                }
            }
        }
    }
}

@Composable
private fun UserInfoDetail(userViewModel: UserViewModel) {
    val user by userViewModel.userInfo
    val context = LocalContext.current

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
                text = "用户信息",
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.secondary
            )
            Spacer(modifier = Modifier.height(5.dp))
            WeCardListItem("用户账号", user?.username)
            WeCardListItem("用户昵称", user?.nick, onClick = {
                if (GlobalState.userInfo.value?.uid != user?.uid) return@WeCardListItem
                dialogServiceInstance.openEditUserNickDialog(
                    context = context,
                    nick = user?.nick.toString()
                ) { value ->
                    userViewModel.updateUserNick(value)
                }
            })
            WeCardListItem("注册时间", user?.regTime)
            WeCardListItem("禁言状态", if (user?.status == UserAuthType.TABOO_STATUS) "是" else "否")
            if (GlobalState.userInfo.value?.uid == user?.uid) {
                WeCardListItem("用户密码", "点击以修改", onClick = {
                    dialogServiceInstance.openEditPasswordDialog(context) { value ->
                        userViewModel.updatePassword(value)
                    }
                })
                WeCardListItem("绑定管理", "点击以查看", onClick = {
                    dialogServiceInstance.openUserBindManageDialog(context)
                })
            }
            WeCardListItem("发言限制", "三秒钟内只能发言一次")
        }
    }
}

@Composable
private fun UserAction(userViewModel: UserViewModel) {
    val context = LocalContext.current
    ActionButton(
        name = "退出当前账号",
        color = colorResource(R.color.app_error),
        click = {
            dialogServiceInstance.openExitLoginDialog(context)
        }
    )
}

@Composable
private fun ActionButton(
    name: String,
    color: Color,
    click: () -> Unit
) {
    Box(
        modifier = Modifier
            .height(50.dp)
            .background(
                shape = RoundedCornerShape(6.dp),
                color = MaterialTheme.colorScheme.onBackground
            )
            .clickable(
                onClick = click
            )
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = name,
            color = color,
            fontSize = 18.sp
        )
    }
}