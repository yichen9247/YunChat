package com.android.yunchat.service.impl

import android.os.Build
import com.android.yunchat.core.SocketConstants
import com.android.yunchat.model.GroupInfoModel
import com.android.yunchat.model.MessageInfoModel
import com.android.yunchat.model.ResultModel
import com.android.yunchat.model.UserInfoModel
import com.android.yunchat.screen.chat.view.ChatViewModel
import com.android.yunchat.service.ClientService
import com.android.yunchat.service.instance.requestServiceInstance
import com.android.yunchat.utils.JsonUtils
import com.google.gson.reflect.TypeToken
import com.xuexiang.xutil.XUtil
import org.json.JSONArray
import org.json.JSONObject
import top.chengdongqing.weui.core.utils.showToast

/**
 * @name 客户服务实现类
 * @author yichen9247
 */
class ClientServiceImpl: ClientService {
    /**
     * @name 发送socket事件
     * @param event 事件名
     * @param data 事件数据
     * @param callback 回调
     */
    inline fun <reified T> sendSocketEmit(
        event: String,
        chatViewModel: ChatViewModel,
        vararg data: Any? = arrayOf(JSONObject()),
        noinline callback: (ResultModel<T>?) -> Unit = { _ -> }
    ) {
        chatViewModel.socket?.let {
            it.emit(event, data) { args -> analysisMessage<T>(
                args = args,
                callback = callback
            )}
        }
    }

    /**
     * @name 解析消息内容
     * @param args 响应参数
     * @param callback 回调
     */
    inline fun <reified T> analysisMessage(
        vararg args: Any,
        noinline callback: (ResultModel<T>?) -> Unit = { _ -> }
    ) {
        val context = XUtil.getContext()
        runCatching {
            val typeToken = object : TypeToken<T>() {}
            val response = args.firstOrNull() as? JSONObject
                ?: throw IllegalArgumentException("无效响应格式")
            val resultType = TypeToken.getParameterized(
                ResultModel::class.java,
                typeToken.type
            ).type
            JsonUtils.fromJson<ResultModel<T>>(response.toString(), resultType).also {
                requestServiceInstance.checkResponseResult(
                    response = it,
                    message = "消息解析失败",
                    onSuccess = { callback(it) }
                )
            }
        }.onFailure {
            when (it) {
                is IllegalArgumentException -> context.showToast("无效响应格式")
                else -> context.showToast("消息解析失败")
            }
            callback(null)
        }
    }

    /**
     * @name 初始化当前平台
     * @param chatViewModel 聊天ViewModel
     */
    override fun clientOnlineLogin(chatViewModel: ChatViewModel) {
        sendSocketEmit<Any>(SocketConstants.Events.ONLINE_LOGIN, chatViewModel, JSONObject().apply {
            put("status", 1)
            put("platform", "Android ${Build.VERSION.RELEASE}")
        })
    }

    /**
     * @name 初始化用户列表
     * @param chatViewModel 聊天ViewModel
     */
    override fun initClientUserList(chatViewModel: ChatViewModel) {
        sendSocketEmit<List<UserInfoModel>>(SocketConstants.Events.GET_USER_LIST, chatViewModel, JSONObject()) { response ->
            response?.data?.let {
                chatViewModel.userList.clear()
                chatViewModel.userList.addAll(it)
            }
        }
    }

    /**
     * @name 初始化消息列表
     * @param gid 群聊id
     * @param chatViewModel 聊天ViewModel
     */
    fun initClientMessageList(
        gid: Int,
        chatViewModel: ChatViewModel
    ) {
        sendSocketEmit<List<MessageInfoModel>>(SocketConstants.Events.GET_MESSAGE_LIST, chatViewModel, JSONObject().apply {
            put("obj", 0)
            put("tar", gid)
        }) { response ->
            response?.data?.let {
                chatViewModel.messageList.clear()
                chatViewModel.messageList.addAll(it)
                chatViewModel.connection.value = true
            }
        }
    }

    /**
     * @name 加入群聊消息房间
     * @param list 群聊id列表
     * @param chatViewModel 聊天ViewModel
     */
    override fun joinGroupMessageRoom(
        list: List<Int>,
        chatViewModel: ChatViewModel
    ) {
        sendSocketEmit<List<UserInfoModel>>(SocketConstants.Events.JOIN_MSG_ROOM, chatViewModel, JSONObject().apply {
            put("type", "group")
            put("list", JSONArray(list))
        })
    }

    /**
     * @name 加入群聊房间
     * @param gid 群聊id
     * @param chatViewModel 聊天ViewModel
     */
    override fun joinGroupRoom(
        gid: Int,
        chatViewModel: ChatViewModel
    ) {
        sendSocketEmit<GroupInfoModel>(SocketConstants.Events.JOIN_ROOM, chatViewModel, JSONObject().apply {
            put("data", gid)
            put("type", "group")
        })
    }


    /**
     * @name 重置AI消息
     * @param chatViewModel 聊天ViewModel
     */
    override fun resetAiMessage(chatViewModel: ChatViewModel) {
        sendSocketEmit<GroupInfoModel>(SocketConstants.Events.RESET_AI_MESSAGE, chatViewModel, JSONObject()) {
            chatViewModel.messageList.clear()
        }
    }
}