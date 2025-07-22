package com.android.yunchat.service

import com.android.yunchat.model.MessageInfoModel
import com.android.yunchat.screen.chat.view.ChatViewModel

interface EventService {
    fun onConnected(chatViewModel: ChatViewModel)
    fun onUserChatMessage(
        vararg args: Any,
        chatViewModel: ChatViewModel
    )
    fun onMessageReceived(
        notice: Boolean,
        message: MessageInfoModel,
        chatViewModel: ChatViewModel
    )
    fun onUserNickUpdate(
        vararg args: Any,
        chatViewModel: ChatViewModel
    )
    fun onUserAvatarUpdate(
        vararg args: Any,
        chatViewModel: ChatViewModel
    )
    fun onCreateAiMessage(
        vararg args: Any,
        chatViewModel: ChatViewModel
    )
}