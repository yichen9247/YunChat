package com.android.yunchat.model

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.android.yunchat.types.UserAuthType

data class HomeDrawerMenuItemModel(
    val key: String,
    val name: String,
    val icon: ImageVector,
    val type: Int = UserAuthType.USER_AUTHENTICATION,
    val call: (context: Context) -> Unit
)

data class GeneralListItemModel(
    val icon: Int,
    val key: String,
    val name: String,
    val call: (context: Context) -> Unit
)

data class MessageColors(
    val content: Color,
    val background: Color
)