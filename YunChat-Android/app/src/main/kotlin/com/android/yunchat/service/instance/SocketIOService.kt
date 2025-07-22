package com.android.yunchat.service.instance

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.android.yunchat.config.AppConfig
import com.android.yunchat.core.SocketConstants
import com.android.yunchat.screen.chat.view.ChatViewModel
import com.android.yunchat.utils.ConsoleUtils
import com.xuexiang.xutil.XUtil.runOnUiThread
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import top.chengdongqing.weui.core.utils.showToast
import java.lang.ref.WeakReference

/**
 * @name 通信服务实现类
 * @author yichen9247
 * @description 核心类，勿动
 */
class SocketIOService: Service() {
    private val binder = LocalBinder()
    private var chatViewModelRef: WeakReference<ChatViewModel>? = null
    private val chatViewModel: ChatViewModel? get() = chatViewModelRef?.get()
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    inner class LocalBinder : Binder() {
        fun getService(): SocketIOService = this@SocketIOService
    }

    fun setChatViewModel(viewModel: ChatViewModel) {
        this.chatViewModelRef = WeakReference(viewModel).apply {
            initializeSocket()
        }
    }

    override fun onDestroy() {
        disconnectSocket()
        serviceScope.cancel()
        chatViewModelRef = null
        super.onDestroy()
    }
    override fun onBind(intent: Intent?): IBinder = binder
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        initializeSocket()
        mediaServiceInstance.initMediaPlayer()
        return START_STICKY
    }

    private fun disconnectSocket() {
        mediaServiceInstance.releaseMediaPlayer()
        chatViewModel?.socket?.let {
            try {
                if (it.connected()) it.disconnect()
                it.off()
            } catch (e: Exception) {
                ConsoleUtils.printInfoLog(e.message.toString())
            } finally {
                chatViewModel?.socket = null
            }
        }
    }

    private fun initializeSocket() {
        serviceScope.launch {
            chatViewModel?.connection?.value = false
            try {
                val options = chatServiceInstance.createSocketOptions()
                val newSocket = IO.socket(AppConfig.socketUrl, options).apply {
                    setupEventListeners()
                    connect()
                }
                chatViewModel?.socket = newSocket
            } catch (e: Exception) {
                runOnUiThread {
                    showToast("连接失败: ${e.message}")
                }
            }
        }
    }

    private fun Socket.setupEventListeners() {
        on(SocketConstants.Events.CONNECT) {
            chatViewModel?.let {
                eventServiceInstance.onConnected(it)
            }
        }

        on(SocketConstants.Events.DISCONNECT) {
            runOnUiThread {
                chatViewModel?.connection?.value = false
            }
        }

        on(SocketConstants.Events.RECE_RE_USER_ALL) {
            chatViewModel?.let {
                clientServiceInstance.initClientUserList(it)
            }
        }

        on(SocketConstants.Events.RECE_AI_CREATE_MESSAGE) { args ->
            chatViewModel?.let {
                eventServiceInstance.onCreateAiMessage(
                    args = args,
                    chatViewModel = it
                )
            }
        }

        on(SocketConstants.Events.RECE_CLIENT_MESSAGE) { args ->
            chatViewModel?.let {
                eventServiceInstance.onUserChatMessage(
                    args = args,
                    chatViewModel = it
                )
            }
        }

        on(SocketConstants.Events.RECE_RE_USER_NICK) { args ->
            chatViewModel?.let {
                eventServiceInstance.onUserNickUpdate(
                    args = args,
                    chatViewModel = it
                )
            }
        }

        on(SocketConstants.Events.RECE_RE_USER_AVATAR) { args ->
            chatViewModel?.let {
                eventServiceInstance.onUserAvatarUpdate(
                    args = args,
                    chatViewModel = it
                )
            }
        }
    }
}