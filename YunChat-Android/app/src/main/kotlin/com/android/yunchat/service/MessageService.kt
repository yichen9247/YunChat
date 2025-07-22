package com.android.yunchat.service

import com.android.yunchat.screen.chat.view.ChatViewModel

interface MessageService {
    fun sendTextMessage(content: String, chatViewModel: ChatViewModel)
    fun sendFileMessage(content: String, chatViewModel: ChatViewModel)
    fun sendImageMessage(content: String, chatViewModel: ChatViewModel)
    fun sendVideoMessage(content: String, chatViewModel: ChatViewModel)
    fun sendAgentMessage(content: String, chatViewModel: ChatViewModel)
    fun getMessageType(
        length: Int,
        type: String,
        content: String
    ): String
}