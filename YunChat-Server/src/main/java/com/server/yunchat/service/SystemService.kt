package com.server.yunchat.service

interface SystemService  {
    fun getSystemUptime(): String
    fun getSystemMemory(): String
}