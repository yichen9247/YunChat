package com.android.yunchat.service.impl

import com.android.yunchat.model.CreateAiMessageModel
import com.android.yunchat.model.MessageInfoModel
import com.android.yunchat.model.UserInfoModel
import com.android.yunchat.screen.chat.view.ChatViewModel
import com.android.yunchat.service.EventService
import com.android.yunchat.service.instance.clientServiceInstance
import com.android.yunchat.service.instance.eventServiceInstance
import com.android.yunchat.service.instance.mediaServiceInstance
import com.android.yunchat.service.instance.userServiceInstance

/**
 * @name 事件处理服务类
 * @author yichen9247
 */
class EventServiceImpl: EventService {
    /**
     * @name 接收连接事件
     * @param chatViewModel 聊天ViewModel
     */
    override fun onConnected(chatViewModel: ChatViewModel) {
        if (chatViewModel.isAgent.value) {
            chatViewModel.connection.value = true
            return
        }
        val gid =  chatViewModel.currentGroupId.intValue
        clientServiceInstance.clientOnlineLogin(chatViewModel)
        clientServiceInstance.initClientUserList(chatViewModel)
        clientServiceInstance.joinGroupRoom(gid, chatViewModel)
        clientServiceInstance.initClientMessageList(gid, chatViewModel)
    }

    /**
     * @name 接收消息事件
     * @param notice 是否通知
     * @param message 消息对象
     * @param chatViewModel 聊天ViewModel
     */
    override fun onMessageReceived(
        notice: Boolean,
        message: MessageInfoModel,
        chatViewModel: ChatViewModel
    ) {
        chatViewModel.messageList.add(message)
        if (!notice || message.uid == userServiceInstance.getUserUid()) return
        mediaServiceInstance.playMessageVoice()
    }

    /**
     * @name 接收用户昵称更新
     * @param args 参数
     * @param chatViewModel 聊天ViewModel
     */
    override fun onUserNickUpdate(
        vararg args: Any,
        chatViewModel: ChatViewModel
    ) {
        clientServiceInstance.analysisMessage<UserInfoModel>(
            args = args,
            callback = { response ->
                response?.data?.let { user ->
                    chatViewModel.userList.find { it.uid == user.uid }?.nick = user.nick
                }
            }
        )
    }

    /**
     * @name 接收用户头像更新
     * @param args 参数
     * @param chatViewModel 聊天ViewModel
     */
    override fun onUserAvatarUpdate(
        vararg args: Any,
        chatViewModel: ChatViewModel
    ) {
        clientServiceInstance.analysisMessage<UserInfoModel>(
            args = args,
            callback = { response ->
                response?.data?.let { user ->
                    chatViewModel.userList.find { it.uid == user.uid }?.avatar = user.avatar
                }
            }
        )
    }

    /**
     * @name 接收用户消息
     * @param args 参数
     * @param chatViewModel 聊天ViewModel
     */
    override fun onUserChatMessage(
        vararg args: Any,
        chatViewModel: ChatViewModel
    ) {
        clientServiceInstance.analysisMessage<MessageInfoModel>(
            args = args,
            callback = { response ->
                response?.data?.let { message ->
                    chatViewModel.let {
                        eventServiceInstance.onMessageReceived(true, message, it)
                    }
                }
            }
        )
    }

    /**
     * @name 接收AI消息
     * @param args 参数
     * @param chatViewModel 聊天ViewModel
     */
    override fun onCreateAiMessage(
        vararg args: Any,
        chatViewModel: ChatViewModel
    ) {
        clientServiceInstance.analysisMessage<CreateAiMessageModel>(
            args = args,
            callback = { response ->
                response?.data?.let {
                    when(it.event) {
                        "PUSH-STREAM" -> {
                            val eventId = it.eventId
                            val currentMessages = chatViewModel.messageList.toMutableList()
                            val messageIndex = currentMessages.indexOfFirst { it.sid == eventId }
                            if (messageIndex != -1) {
                                currentMessages[messageIndex] = currentMessages[messageIndex].run {
                                    val updatedContent = if (content == "正在请求中") {
                                        it.content
                                    } else content + it.content
                                    copy(content = updatedContent)
                                }
                                chatViewModel.messageList.apply {
                                    clear()
                                    addAll(currentMessages)
                                }
                            }
                        }
                        "CREATE-MESSAGE" -> {
                            eventServiceInstance.onMessageReceived(false, it.result, chatViewModel)
                        }
                    }
                }
            }
        )
    }
}