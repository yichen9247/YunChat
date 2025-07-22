package com.server.yunchat.socket.service

import com.corundumstudio.socketio.AckRequest
import com.corundumstudio.socketio.SocketIOClient
import com.server.yunchat.builder.props.YunChatProps
import com.server.yunchat.builder.service.AuthObjService
import com.server.yunchat.builder.types.UserAuthType
import com.server.yunchat.builder.utils.ConsoleUtils
import com.server.yunchat.builder.utils.GlobalService
import com.server.yunchat.builder.utils.ResultUtils
import com.server.yunchat.service.impl.ClientServiceImpl
import org.springframework.aop.support.AopUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.lang.reflect.InaccessibleObjectException
import java.lang.reflect.Method

@Service
class SocketEventService @Autowired constructor(
    private val yunChatProps: YunChatProps,
    private val authObjService: AuthObjService,
    private val clientServiceImpl: ClientServiceImpl,
) {
    private val registeredMethods = mutableSetOf<Method>()

    fun addSocketEventListener(instance: Any) {
        val targetClass = AopUtils.getTargetClass(instance)
        targetClass.declaredMethods.forEach { method ->
            method.getAnnotationsByType(SocketEventListener::class.java).forEach { annotation ->
                if (!registeredMethods.add(method)) return
                registerSocketListener(instance, method, annotation)
            }
        }
    }

    private fun registerSocketListener(instance: Any, method: Method, annotation: SocketEventListener) {
        method.isAccessible = true
        val eventClass = annotation.eventClass.java
        validateMethodParameters(method, eventClass)
        GlobalService.socketIOServer?.apply {
            addEventListener(annotation.event, eventClass) { client, data, ackSender ->
                handleSocketEvent(data, instance, method, ackSender, client, annotation)
            }
        } ?: ConsoleUtils.printErrorLog("SocketIO server not initialized")
    }

    private fun handleSocketEvent(
        data: Any?, instance: Any,
        method: Method, ackSender: AckRequest, client:
        SocketIOClient, annotation: SocketEventListener
    ) {
        try {
            if (annotation.eventClass != Any::class.java) validateEventData(
                data = data, client = client,
                eventClass = annotation.eventClass.java
            )

            if (clientServiceImpl.getSocketClientVersion(client) == yunChatProps.appVersion) {
                val userAuthorization = authObjService.validClientTokenBySocket(client)
                when (annotation.permission) {
                    UserAuthType.USER_AUTHENTICATION -> {
                        if (userAuthorization == null) {
                            ackSender.sendAckData(ResultUtils.printForbiddenMessage())
                            return
                        }
                    }
                    UserAuthType.ADMIN_AUTHENTICATION -> {
                        if (userAuthorization == null ||
                            authObjService.getUserAuthInfo(userAuthorization.uid) != UserAuthType.ADMIN_AUTHENTICATION) {
                            ackSender.sendAckData(ResultUtils.printForbiddenMessage())
                            return
                        }
                    }
                }
                method.invoke(instance, *buildMethodArguments(
                    userAuthorization?.uid ?: 0L, data, method, ackSender, client
                ))
            } else ackSender.sendAckData(ResultUtils.printVersionMessage())
        } catch (e: Exception) {
            ackSender.sendAckData(ResultUtils.printErrorMessage(e))
        }
    }

    private fun buildMethodArguments(
        uid: Long, data: Any?, method: Method,
        ackSender: AckRequest, client: SocketIOClient
    ): Array<Any?> = method.parameters.map { param ->
        when {
            param.type.isAssignableFrom(Long::class.java) -> uid
            param.type.isAssignableFrom(AckRequest::class.java) -> ackSender
            param.type.isAssignableFrom(SocketIOClient::class.java) -> client
            else -> data
        }
    }.toTypedArray()

    private fun validateMethodParameters(method: Method, eventClass: Class<*>) {
        var dataParams = 0
        method.parameters.forEach { param ->
            when {
                param.type.isAssignableFrom(AckRequest::class.java) -> {}
                param.type.isAssignableFrom(SocketIOClient::class.java) -> {}
                eventClass == Any::class.java -> dataParams++
                param.type.isAssignableFrom(eventClass) -> dataParams++
            }
        }
        require(dataParams <= 1) { "Method ${method.name} must have at most one data parameter" }
    }
}

private fun validateEventData(client: SocketIOClient, data: Any?, eventClass: Class<*>) {
    requireNotNull(data) { "参数不能为空" }

    if (data !is Map<*, *>) {
        try {
            data.javaClass.declaredFields.associate { field ->
                field.isAccessible = true
                field.name to field.get(data)
            }
        } catch (e: InaccessibleObjectException) {
            client.disconnect() // 拒绝连接
            throw IllegalArgumentException("参数必须为JSON对象")
        }
    }

    for (field in eventClass.declaredFields.toList()) {
        field.isAccessible = true
        val value = try {
            field.get(data)
        } catch (e: IllegalAccessException) {
            throw IllegalArgumentException("无法访问字段 ${field.name}")
        }
        if (value == null) throw IllegalArgumentException("字段 ${field.name} 不能为空")
    }
}