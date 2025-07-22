package com.android.yunchat.service.instance

import RouteServiceImpl
import com.android.yunchat.service.impl.ActivityServiceImpl
import com.android.yunchat.service.impl.ChatServiceImpl
import com.android.yunchat.service.impl.ClientServiceImpl
import com.android.yunchat.service.impl.DialogServiceImpl
import com.android.yunchat.service.impl.EncryptServiceImpl
import com.android.yunchat.service.impl.EventServiceImpl
import com.android.yunchat.service.impl.FileServiceImpl
import com.android.yunchat.service.impl.MediaServiceImpl
import com.android.yunchat.service.impl.MessageServiceImpl
import com.android.yunchat.service.impl.NoticeServiceImpl
import com.android.yunchat.service.impl.RequestServiceImpl
import com.android.yunchat.service.impl.StorageServiceImpl
import com.android.yunchat.service.impl.SystemServiceImpl
import com.android.yunchat.service.impl.UploadServiceImpl
import com.android.yunchat.service.impl.UserServiceImpl

val userServiceInstance = UserServiceImpl()
val fileServiceInstance = FileServiceImpl()
val chatServiceInstance = ChatServiceImpl()
val routeServiceInStance = RouteServiceImpl()
val mediaServiceInstance = MediaServiceImpl()
val eventServiceInstance = EventServiceImpl()
val noticeServiceInstance = NoticeServiceImpl()
val systemServiceInstance = SystemServiceImpl()
val dialogServiceInstance = DialogServiceImpl()
val clientServiceInstance = ClientServiceImpl()
val uploadServiceInstance = UploadServiceImpl()
val messageServiceInstance = MessageServiceImpl()
val requestServiceInstance = RequestServiceImpl()
val storageServiceInstance = StorageServiceImpl()
val encryptServiceInstance = EncryptServiceImpl()
val activityServiceInstance = ActivityServiceImpl()
