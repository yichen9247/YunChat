package com.android.yunchat.service.impl

import com.android.yunchat.core.SocketConstants
import com.android.yunchat.model.MessageInfoModel
import com.android.yunchat.screen.chat.view.ChatViewModel
import com.android.yunchat.service.MessageService
import com.android.yunchat.service.instance.clientServiceInstance
import com.xuexiang.xutil.XUtil
import org.json.JSONObject
import top.chengdongqing.weui.core.utils.showToast

/**
 * @name 消息服务实现类
 * @author yichen9247
 */
class MessageServiceImpl: MessageService {
    /**
     * @name 发送消息
     * @param content 消息内容
     */
    private fun sendChatMessage(
        content: String,
        type: String = "text",
        autoPush: Boolean = false,
        chatViewModel: ChatViewModel,
        event: String = SocketConstants.Events.SEND_MESSAGE
    ) {
        if (content.trim().isEmpty()) {
            return XUtil.getContext().showToast("内容不能为空")
        }
        clientServiceInstance.sendSocketEmit<MessageInfoModel>(event, chatViewModel, JSONObject().apply {
            put("obj", 0)
            put("type", type)
            put("content", content)
            put("tar", chatViewModel.currentGroupId.intValue)
        }) { response ->
            if (!autoPush) return@sendSocketEmit
            response?.data?.let {
                chatViewModel.messageList.add(it)
            }
        }
        chatViewModel.inputValue.value = ""
    }

    /**
     * @name 发送文本消息
     * @param content 消息内容
     */
    override fun sendTextMessage(content: String, chatViewModel: ChatViewModel) {
        sendChatMessage(
            autoPush = false,
            content = content,
            chatViewModel = chatViewModel
        )
    }

    /**
     * @name 发送文件消息
     * @param content 消息内容
     */
    override fun sendFileMessage(content: String, chatViewModel: ChatViewModel) {
        sendChatMessage(
            type = "file",
            autoPush = false,
            content = content,
            chatViewModel = chatViewModel
        )
    }

    /**
     * @name 发送图片消息
     * @param content 消息内容
     */
    override fun sendImageMessage(content: String, chatViewModel: ChatViewModel) {
        sendChatMessage(
            type = "image",
            autoPush = false,
            content = content,
            chatViewModel = chatViewModel
        )
    }

    /**
     * @name 发送文本消息
     * @param content 消息内容
     */
    override fun sendVideoMessage(content: String, chatViewModel: ChatViewModel) {
        sendChatMessage(
            type = "video",
            autoPush = false,
            content = content,
            chatViewModel = chatViewModel
        )
    }

    /**
     * @name 发送智能消息
     * @param content 消息内容
     */
    override fun sendAgentMessage(content: String, chatViewModel: ChatViewModel) {
        sendChatMessage(
            autoPush = true,
            content = content,
            chatViewModel = chatViewModel,
            event = SocketConstants.Events.SEND_AI_MESSAGE
        )
    }

    override fun getMessageType(
        length: Int,
        type: String,
        content: String
    ): String {
        return when (type) {
            "text" -> content.take(length)
            "file" -> "[文件消息]"
            "image" -> "[图片消息]"
            "video" -> "[视频消息]"
            "invite" -> "[分享群聊]"
            else -> "[未知消息]"
        }
    }
}