package com.server.yunchat.socket.service

import com.server.yunchat.builder.types.UserAuthType
import kotlin.reflect.KClass

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class SocketEventListener(
    val event: String,
    val eventClass: KClass<*> = Any::class,
    val permission: Int = UserAuthType.VISITOR_AUTHENTICATION,
)