package com.android.yunchat.screen.chat.view

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.activity.result.ActivityResult
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.yunchat.model.MessageInfoModel
import com.android.yunchat.model.UserInfoModel
import com.android.yunchat.service.instance.SocketIOService
import com.android.yunchat.service.instance.messageServiceInstance
import com.android.yunchat.service.instance.uploadServiceInstance
import com.android.yunchat.utils.ConsoleUtils
import com.xuexiang.xutil.XUtil
import io.socket.client.Socket
import kotlinx.coroutines.launch
import top.chengdongqing.weui.core.data.model.MediaItem
import top.chengdongqing.weui.core.ui.components.toast.ToastState
import top.chengdongqing.weui.core.utils.showToast
import java.lang.ref.WeakReference

/**
 * @name 聊天ViewModel
 * @author yichen9247
 */
class ChatViewModel: ViewModel() {
    val isAgent = mutableStateOf(false)
    val inputValue = mutableStateOf("")
    val isMoreOpen = mutableStateOf(false)
    val connection = mutableStateOf(false)
    val currentGroupId = mutableIntStateOf(1)
    val userList: MutableList<UserInfoModel> = mutableStateListOf()
    val messageList: MutableList<MessageInfoModel> = mutableStateListOf()

    var socket: Socket? = null
    private var serviceBound = false
    private val contextRef = WeakReference(XUtil.getContext().applicationContext)
    private val context: Context get() = contextRef.get() ?: throw IllegalStateException("Context unavailable")

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            try {
                val service = (binder as SocketIOService.LocalBinder).getService()
                service.setChatViewModel(this@ChatViewModel)
                serviceBound = true
            } catch (_: Exception) {
                context.showToast("服务连接失败")
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            serviceBound = false
        }
    }

    override fun onCleared() {
        stopSocketService()
        super.onCleared()
    }

    fun bindServiceIfNeeded() {
        if (serviceBound) return
        try {
            val intent = Intent(context, SocketIOService::class.java).apply {
                `package` = context.packageName
            }
            context.startService(intent)
            serviceBound = context.bindService(
                intent,
                serviceConnection,
                Context.BIND_AUTO_CREATE or Context.BIND_IMPORTANT
            )
            if (!serviceBound)context.showToast("服务绑定失败")
        } catch (e: Exception) {
            context.showToast("服务绑定异常：${e.message}")
        }
    }

    private fun unbindService() {
        if (serviceBound) {
            try {
                context.unbindService(serviceConnection)
            } catch (e: Exception) {
                ConsoleUtils.printInfoLog(e)
            } finally {
                serviceBound = false
            }
        }
    }

    fun stopSocketService() {
        unbindService()
        try {
            context.stopService(Intent(context, SocketIOService::class.java))
        } catch (e: Exception) {
            ConsoleUtils.printInfoLog(e)
        }
    }

    fun sendTextMessage() {
        messageServiceInstance.sendTextMessage(inputValue.value, this)
    }

    fun sendAgentMessage() {
        messageServiceInstance.sendAgentMessage(inputValue.value, this)
    }

    fun handleSelectFile(result: ActivityResult, toastState: ToastState) {
        viewModelScope.launch {
            uploadServiceInstance.handleSelectFile(result, toastState) {
                messageServiceInstance.sendFileMessage(it, this@ChatViewModel)
            }
        }
    }

    fun handleSelectImage(list: Array<MediaItem>, toastState: ToastState) {
        viewModelScope.launch {
            uploadServiceInstance.handleSelectImage(list.first().uri, toastState) {
                messageServiceInstance.sendImageMessage(it, this@ChatViewModel)
            }
        }
    }

    fun handleSelectVideo(list: Array<MediaItem>, toastState: ToastState) {
        viewModelScope.launch {
            uploadServiceInstance.handleSelectVideo(list.first().uri, toastState) {
                messageServiceInstance.sendVideoMessage(it, this@ChatViewModel)
            }
        }
    }
}