package com.android.yunchat.service

import io.socket.client.IO

interface ChatService {
    fun createSocketOptions(): IO.Options
    fun createAuthHeaders(): Map<String, String>
}