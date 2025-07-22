package com.android.yunchat.config

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.InsertDriveFile
import androidx.compose.material.icons.outlined.KeyboardVoice
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.ManageAccounts
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.QrCode
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.VideoCameraFront
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import com.android.yunchat.R
import com.android.yunchat.activity.AboutActivity
import com.android.yunchat.activity.AdminActivity
import com.android.yunchat.activity.AgentActivity
import com.android.yunchat.activity.NoticeActivity
import com.android.yunchat.activity.SettingActivity
import com.android.yunchat.activity.UserActivity
import com.android.yunchat.activity.VoiceActivity
import com.android.yunchat.model.GeneralListItemModel
import com.android.yunchat.model.HomeDrawerMenuItemModel
import com.android.yunchat.service.instance.activityServiceInstance
import com.android.yunchat.service.instance.dialogServiceInstance
import com.android.yunchat.service.instance.userServiceInstance
import com.android.yunchat.types.UserAuthType
import com.xuexiang.xutil.XUtil
import top.chengdongqing.weui.core.ui.components.radio.RadioOption
import top.chengdongqing.weui.core.utils.showToast

/**
 * @name UI配置类
 * @author yichen9247
 */
object UIConfig {
    val homeDrawerMenuList = listOf(
        HomeDrawerMenuItemModel(
            key = "notice",
            name = "通知公告",
            icon = Icons.Outlined.Notifications,
            call = { context ->
                activityServiceInstance.intentActivityAboutContext(
                    context = context,
                    activity = NoticeActivity::class.java
                )
            }
        ),
        HomeDrawerMenuItemModel(
            key = "scan",
            name = "在线扫码",
            icon = Icons.Outlined.QrCode,
            call = {}
        ),
        HomeDrawerMenuItemModel(
            key = "search",
            name = "搜索群聊",
            icon = Icons.Outlined.Search,
            call = { context ->
                dialogServiceInstance.openSearchGroupDialog(context)
            }
        ),
        HomeDrawerMenuItemModel(
            key = "agent",
            name = "智能聊天",
            icon = Icons.Outlined.Face,
            call = { context ->
                activityServiceInstance.intentActivityAboutContext(
                    context = context,
                    activity = AgentActivity::class.java
                )
            }
        ),
        HomeDrawerMenuItemModel(
            key = "admin",
            name = "管理后台",
            icon = Icons.Outlined.ManageAccounts,
            type = UserAuthType.ADMIN_AUTHENTICATION,
            call = { context ->
                activityServiceInstance.intentActivityAboutContext(
                    context = context,
                    activity = AdminActivity::class.java
                )
            }
        ),
        HomeDrawerMenuItemModel(
            key = "setting",
            name = "软件设置",
            icon = Icons.Outlined.Settings,
            call = { context ->
                activityServiceInstance.intentActivityAboutContext(
                    context = context,
                    activity = SettingActivity::class.java
                )
            }
        ),
        HomeDrawerMenuItemModel(
            key = "exit",
            name = "退出登录",
            icon = Icons.Outlined.Logout,
            call = { context ->
                dialogServiceInstance.openExitLoginDialog(context)
            }
        ),
        HomeDrawerMenuItemModel(
            key = "about",
            name = "关于软件",
            icon = Icons.Outlined.Info,
            call = { context ->
                activityServiceInstance.intentActivityAboutContext(
                    context = context,
                    activity = AboutActivity::class.java
                )
            }
        )
    )

    val chatFunctionList = listOf(
        "发送语音" to Icons.Outlined.KeyboardVoice,
        "发送文件" to Icons.Outlined.InsertDriveFile,
        "发送图片" to Icons.Outlined.Image,
        "发送视频" to Icons.Outlined.VideoCameraFront
    )

    val supportMessageType = listOf<String>("text", "video", "invite", "image", "file")

    val chatEmojiCharList = listOf(
        // Smileys & Emotion
        "😀", "😃", "😄", "😁", "😆", "😅", "😂", "🤣", "😊", "😇", "🙂", "🙃", "😉", "😌", "😍",
        "😘", "😗", "😙", "😚", "😋", "😛", "😝", "😜", "🤪", "🤨", "🧐", "🤓", "😎", "🤩", "😏",

        // Negative Emotions
        "😒", "😞", "😔", "😟", "😕", "🙁", "😣", "😖", "😫", "😩", "😢", "😭", "😮", "💨", "😤",
        "😠", "😡", "🤬", "🤯", "😳", "😱", "😨", "😰", "😥", "😓",

        // Face Actions
        "🤗", "🤔", "🤭", "🤫", "🤥", "😶", "😾", "😐", "😑", "😬", "🙄", "😯", "😦", "😧", "😮",
        "😲", "😴", "🤤", "😪", "😵", "💫", "🤐", "🤢", "🤮", "🤧",

        // Special Characters
        "😷", "🤒", "🤕", "🤑", "🤠", "😈", "👿", "👹", "👺", "🤡", "💩", "👻", "💀", "👽", "👾",
        "🤖", "🎃", "😺", "😸", "😹", "😻", "😼", "😽", "🙀", "😿"
    )

    val appBarModifier = Modifier
        .shadow(
            clip = false,
            elevation = 4.dp,
            shape = RectangleShape,
            spotColor = Color.Gray,
            ambientColor = Color.Gray
        )

    val homeNavigationBarList = listOf(
        GeneralListItemModel(
            key = "home",
            name = "首页",
            icon = R.drawable.ic_action_home,
            call = {}
        ),
        GeneralListItemModel(
            key = "more",
            name = "更多",
            icon = R.drawable.ic_action_more,
            call = {}
        )
    )

    val audioConfig = mapOf(
        "momo" to R.raw.momo,
        "pcqq" to R.raw.pcqq,
        "apple" to R.raw.apple,
        "huaji" to R.raw.huaji,
        "default" to R.raw.handsock,
        "mobileqq" to R.raw.mobileqq
    )

    val audioOptionList = listOf(
        RadioOption(label = "默认音效", value = "default"),
        RadioOption(label = "苹果音效", value = "apple"),
        RadioOption(label = "陌陌音效", value = "momo"),
        RadioOption(label = "滑稽音效", value = "huaji"),
        RadioOption(label = "系统通知", value = "system")
    )

    val settingProjectList = listOf(
        GeneralListItemModel(
            key = "account",
            name = "账号与安全",
            icon = R.drawable.ic_setting_account,
            call = { context ->
                activityServiceInstance.intentActivityAboutContext(
                    context = context,
                    activity = UserActivity::class.java
                ) {
                    putLong("uid", userServiceInstance.getUserUid())
                }
            }
        ),
        GeneralListItemModel(
            key = "theme",
            name = "个性与主题",
            icon = R.drawable.ic_setting_theme,
            call = {
                XUtil.getContext().showToast("当前功能正在开发中")
            }
        ),
        GeneralListItemModel(
            key = "privacy",
            name = "通知与隐私",
            icon = R.drawable.ic_setting_provacy,
            call = { context ->
                activityServiceInstance.intentActivityAboutContext(
                    context = context,
                    activity = VoiceActivity::class.java
                )
            }
        ),
        GeneralListItemModel(
            key = "cache",
            name = "存储与缓存",
            icon = R.drawable.ic_setting_cache,
            call = { context ->
                dialogServiceInstance.openClearAppCacheDialog(context)
            }
        ),
        GeneralListItemModel(
            key = "about",
            name = "关于与帮助",
            icon = R.drawable.ic_setting_about,
            call = { context ->
                activityServiceInstance.intentActivityAboutContext(
                    context = context,
                    activity = AboutActivity::class.java
                )
            }
        ),
        GeneralListItemModel(
            key = "exit",
            name = "退出此账号",
            icon = R.drawable.ic_setting_exit,
            call = { context ->
                dialogServiceInstance.openExitLoginDialog(context)
            }
        )
    )
}