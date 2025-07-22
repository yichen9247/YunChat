package com.android.yunchat.service

interface MediaService {
    fun initMediaPlayer()
    fun playMessageVoice()
    fun releaseMediaPlayer()
}