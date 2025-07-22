package com.android.yunchat.service

import com.android.yunchat.screen.chat.view.ChatViewModel

interface ClientService {
    fun resetAiMessage(chatViewModel: ChatViewModel)
    fun clientOnlineLogin(chatViewModel: ChatViewModel)
    fun initClientUserList(chatViewModel: ChatViewModel)
    fun joinGroupRoom(gid: Int, chatViewModel: ChatViewModel)
    fun joinGroupMessageRoom(list: List<Int>, chatViewModel: ChatViewModel)
}