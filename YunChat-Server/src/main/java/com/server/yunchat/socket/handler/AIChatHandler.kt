package com.server.yunchat.socket.handler

import com.corundumstudio.socketio.AckRequest
import com.corundumstudio.socketio.SocketIOClient
import com.fasterxml.jackson.databind.ObjectMapper
import com.server.yunchat.admin.service.ServerSystemService
import com.server.yunchat.builder.data.ResultModel
import com.server.yunchat.builder.data.SocketSendMessage
import com.server.yunchat.builder.model.MessageModel
import com.server.yunchat.builder.utils.ConsoleUtils
import com.server.yunchat.builder.utils.HandUtils
import com.server.yunchat.plugin.SpringPlugin
import com.server.yunchat.service.impl.ChatServiceImpl
import com.server.yunchat.service.impl.ClientServiceImpl
import com.server.yunchat.service.impl.UserServiceImpl
import io.micrometer.core.instrument.util.StringEscapeUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Service
class AIChatHandler @Autowired constructor(
    private val springPlugin: SpringPlugin,
    private val chatServiceImpl: ChatServiceImpl,
    private val userServiceImpl: UserServiceImpl,
    private val clientServiceImpl: ClientServiceImpl,
    private val serverSystemService: ServerSystemService,
) {
    private val objectMapper = ObjectMapper()
    private val sendExecutor: ExecutorService = Executors.newFixedThreadPool(4)

    fun handleResetAiMessage(uid: Long): ResultModel {
        userMessageHistory.remove(uid)
        return HandUtils.handleResultByCode(HttpStatus.OK, null, "重置记录成功")
    }

    fun handleAIChatMessage(uid: Long, client: SocketIOClient, data: SocketSendMessage, ackRequest: AckRequest) {
        try {
            springPlugin.validField(data)
            val messageModel = MessageModel()
            val hasAiAuth = userServiceImpl.getUserAiAuth(uid)
            val content = HandUtils.stripHtmlTagsForString(data.content!!)
            data.content = content
            val message = if (hasAiAuth) REQUEST_STATUS else DEFAULT_MESSAGE
            val selfResult = chatServiceImpl.setMessageModel(uid, content, data, messageModel)
            ackRequest.sendAckData(HandUtils.handleResultByCode(HttpStatus.OK, selfResult, "请求成功"))
            Thread.sleep(800) // 延迟800MS，防止AI请求过快
            val agentResult = chatServiceImpl.setMessageModel(1234567890, message, SocketSendMessage(
                obj = 1,
                tar = 1,
                type = "text"
            ), messageModel)
            sendEventWithResult(client, agentResult)
            if (hasAiAuth) sendAIRequest(client, agentResult.sid, uid, content)
        } catch (e: Exception) {
            ackRequest.sendAckData(
                HandUtils.handleResultByCode(HttpStatus.BAD_REQUEST, null, e.message.toString())
            )
        }
    }

    private fun sendEventWithResult(client: SocketIOClient, aiResult: MessageModel) {
        client.sendEvent("[AI:CHAT:CREATE:MESSAGE]",
            HandUtils.handleResultByCode(HttpStatus.OK, object : HashMap<String?, Any?>() {
                init {
                    put("event", "CREATE-MESSAGE")
                    put("result", aiResult)
                }
            }, "请求成功")
        )
    }

    private fun sendAIRequest(client: SocketIOClient, sid: String, uid: Long, userContent: String) {
        val aiUrl = serverSystemService.getSystemKeyValues("ai_url")
        val aiRole = serverSystemService.getSystemKeyValues("ai_role")
        val aiToken = serverSystemService.getSystemKeyValues("ai_token")
        val aiModel = serverSystemService.getSystemKeyValues("ai_model")

        if (aiToken.isEmpty() || aiUrl.isEmpty() || aiModel.isEmpty()) {
            ConsoleUtils.printErrorLog("AI配置缺失: token=$aiToken, url=$aiUrl, model=$aiModel")
            sendErrorResponse(client, sid, "AI服务配置不完整")
            return
        }

        val webClient = WebClient.builder()
            .baseUrl(aiUrl)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer $aiToken")
            .build()

        // 添加当前用户消息
        userMessageHistory.computeIfAbsent(uid) { ArrayList() }.add(userContent)

        // 构建当前消息数组
        val messages = mutableListOf<Map<String, String>>()

        // 添加系统预制消息
        messages.add(mapOf(
            "role" to "system",
            "content" to StringEscapeUtils.escapeJson(aiRole)
        ))

        // 添加历史记录消息
        userMessageHistory[uid]?.forEach { msg ->
            messages.add(mapOf(
                "role" to "user",
                "name" to uid.toString(),
                "content" to StringEscapeUtils.escapeJson(msg)
            ))
        }

        val requestBody = mapOf(
            "stream" to true,
            "model" to aiModel,
            "temperature" to 0.7,
            "messages" to messages
        )

        // 打印请求详情用于调试
        ConsoleUtils.printInfoLog(messages)
        ConsoleUtils.printInfoLog("Sending AI request to: $aiUrl")

        webClient.post()
            .bodyValue(requestBody)
            .retrieve()
            .bodyToFlux(String::class.java)
            .onBackpressureBuffer()
            .delayElements(Duration.ofMillis(50))
            .subscribe(
                { line -> handleResponseLine(client, sid, line) },
                { error ->
                    ConsoleUtils.printErrorLog("请求失败: ${error.message}")
                    sendErrorResponse(client, sid, "请求失败: ${error.message}")
                }, {
                    ConsoleUtils.printCustomLogs(
                        uid = uid,
                        hash = sid,
                        content = "AI Request Completed",
                        address = clientServiceImpl.getSocketClientIp(client)
                    )
                }
            )
    }

    private fun sendErrorResponse(client: SocketIOClient, sid: String, errorMessage: String) {
        val eventData = mapOf(
            "eventId" to sid,
            "event" to "PUSH-STREAM",
            "content" to errorMessage
        )
        client.sendEvent(
            "[AI:CHAT:CREATE:MESSAGE]",
            HandUtils.handleResultByCode(HttpStatus.OK, eventData, "请求成功")
        )
    }

    private fun handleResponseLine(client: SocketIOClient, sid: String, line: String?) {
        if (line.isNullOrBlank()) return
        if ("[DONE]" == line.trim()) return
        try {
            val jsonNode = objectMapper.readTree(line)
            val choices = jsonNode.path("choices")
            if (choices.isArray && choices.size() > 0) {
                val delta = choices[0].path("delta")
                val contentNode = delta.path("content")
                if (!contentNode.isMissingNode && contentNode.isTextual) {
                    val contentText = contentNode.asText()
                    if (contentText.isNotBlank()) {
                        val eventData = mapOf(
                            "event" to "PUSH-STREAM",
                            "eventId" to sid,
                            "content" to contentText
                        )
                        sendExecutor.execute {
                            client.sendEvent("[AI:CHAT:CREATE:MESSAGE]", HandUtils.handleResultByCode(
                                HttpStatus.OK, eventData, "请求成功")
                            )
                        }
                    }
                }
            }
        } catch (e: Exception) {
            ConsoleUtils.printErrorLog("Error parsing AI response: ${e.message}")
            ConsoleUtils.printErrorLog("Problematic line: $line")
        }
    }

    companion object {
        private const val REQUEST_STATUS = "正在请求中"
        private val userMessageHistory: MutableMap<Long, MutableList<String>> = ConcurrentHashMap()
        private const val DEFAULT_MESSAGE = "暂无AI能力相关权限，请前往主群发送【yun apply-ai】以开启AI能力"
    }
}