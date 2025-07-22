package com.android.yunchat.service.impl

import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import com.android.yunchat.R
import com.android.yunchat.config.UIConfig
import com.android.yunchat.service.MediaService
import com.android.yunchat.service.instance.storageServiceInstance
import com.xuexiang.xutil.XUtil
import top.chengdongqing.weui.core.utils.showToast

/**
 * @name 媒体服务实现类
 * @author yichen9247
 */
class MediaServiceImpl : MediaService {
    companion object {
        private var mediaPlayer: MediaPlayer? = null
        private var ringtone: android.media.Ringtone? = null
    }

    /**
     * @name 播放消息通知铃声（使用系统默认通知音）
     */
    override fun playMessageVoice() {
        if (!storageServiceInstance.mmkv.decodeBool("voice", false)) {
            return
        }
        if (storageServiceInstance.mmkv.decodeString("audio", "default").equals("system")) {
            playSystemNotification()
        } else mediaPlayer?.start()
    }

    /**
     * @name 释放媒体播放资源
     */
    override fun releaseMediaPlayer() {
        mediaPlayer?.let {
            it.release()
            mediaPlayer = null
        }
        ringtone?.let {
            if (it.isPlaying) {
                it.stop()
            }
            ringtone = null
        }
    }

    /**
     * @name 初始化MediaPlayer
     */
    override fun initMediaPlayer() {
        releaseMediaPlayer()
        mediaPlayer = MediaPlayer.create(
            XUtil.getContext(),
            UIConfig.audioConfig[
                storageServiceInstance.mmkv.decodeString("audio", "default").toString()
            ] ?: R.raw.handsock
        )
    }

    private fun playSystemNotification() {
        val context = XUtil.getContext()
        try {
            val notificationUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            ringtone = RingtoneManager.getRingtone(context, notificationUri).apply {
                streamType = android.media.AudioManager.STREAM_NOTIFICATION
                play()
            }
        } catch (e: Exception) {
            context.showToast("通知铃声播放失败")
        }
    }
}