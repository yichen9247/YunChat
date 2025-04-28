package com.server.handsock.socket.handler

import com.corundumstudio.socketio.AckRequest
import com.corundumstudio.socketio.SocketIOClient
import com.fasterxml.jackson.databind.ObjectMapper
import com.server.handsock.client.man.ClientChatManage
import com.server.handsock.client.service.ClientUserService
import com.server.handsock.common.data.SocketUserMessage
import com.server.handsock.common.model.MessageModel
import com.server.handsock.common.props.AiProp
import com.server.handsock.common.utils.ConsoleUtils
import com.server.handsock.common.utils.HandUtils
import com.server.handsock.common.utils.IDGenerator
import com.server.handsock.service.AuthService
import com.server.handsock.service.ClientService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Service
class AIChatHandler @Autowired constructor(
    private val authService: AuthService,
    private val clientService: ClientService,
    private val clientUserService: ClientUserService,
) {
    private val objectMapper = ObjectMapper()
    private val sendExecutor: ExecutorService = Executors.newFixedThreadPool(4)

    fun handleAIChatMessage(client: SocketIOClient, data: SocketUserMessage, ackRequest: AckRequest, aiProp: AiProp) {
        authService.validChatMessageStatusBySocket(
            data = data,
            client = client,
            ackRequest = ackRequest,
            call = {
                try {
                    val clientChannelDao = MessageModel()
                    val content = data.content?.let { HandUtils.stripHtmlTagsForString(it) }
                    val clientChatManage = ClientChatManage(HandUtils, ConsoleUtils, IDGenerator)
                    val userResult = clientChatManage.insertChatMessage(clientChannelDao, "text", clientService.getRemoteUID(client), clientService.getRemoteGID(client), clientService.getRemoteAddress(client), content)
                    ackRequest.sendAckData(HandUtils.handleResultByCode(HttpStatus.OK, userResult, "请求成功"))

                    Thread.sleep(800)
                    val robotUser = clientUserService.robotInnerStatus()
                    val hasAiAuth = clientService.hasAiAuthorization(client)
                    val message = if (hasAiAuth) REQUEST_STATUS else DEFAULT_MESSAGE
                    val aiResult = clientChatManage.insertChatMessage(clientChannelDao, "text", robotUser.uid, clientService.getRemoteGID(client), "none", message)
                    sendEventWithResult(client, aiResult)
                    if (hasAiAuth) sendAIRequest(
                        client = client,
                        aiProp = aiProp,
                        userContent = content,
                        sid = aiResult["sid"].toString(),
                        uid = clientService.getRemoteUID(client)
                    )
                } catch (e: Exception) {
                    ackRequest.sendAckData(HandUtils.printErrorLog(e))
                }
            }
        )
    }

    private fun sendEventWithResult(client: SocketIOClient, aiResult: Map<String, Any>) {
        client.sendEvent("[AI:CHAT:CREATE:MESSAGE]",
            HandUtils.handleResultByCode(HttpStatus.OK, object : HashMap<String?, Any?>() {
                init {
                    put("event", "CREATE-MESSAGE")
                    put("result", aiResult)
                }
            }, "请求成功")
        )
    }

    private fun sendAIRequest(client: SocketIOClient, sid: String, uid: Long, userContent: String?, aiProp: AiProp) {
        val webClient = Objects.requireNonNull(aiProp.url)?.let {
            WebClient.builder()
                .baseUrl(it)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + aiProp.token)
                .build()
        }

        userMessageHistory.computeIfAbsent(uid) { ArrayList() }.add(userContent)
        val roleString = "{\"role\": \"system\",\"content\": \"你是HandSock聊天室的高效AI助手，由HandSock开发团队深度优化，专注精准理解与简洁表达。回复严格≤150字，用<think>快速梳理核心逻辑后，先输出<br/>后再输出自然口语化答案。”\"}"
        val roleStringBuilder = StringBuilder(roleString)
        for (msg in userMessageHistory[uid]!!) roleStringBuilder.append(",{\"role\": \"user\",\"name\": \"").append(uid).append("\",\"content\": \"").append(msg).append("\"}")
        val roleStringWithHistory = roleStringBuilder.toString()
        ConsoleUtils.printInfoLog(roleStringWithHistory)

        val requestBody = """{
            "model": "${aiProp.model}",
            "messages": [
                $roleStringWithHistory
            ],
            "response_format": {
                "type": "text"
            },
            "stream": true
        }"""
        if (webClient == null) return

        aiProp.path?.let {
            webClient.post()
                .uri(it)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToFlux(String::class.java)
        }?.subscribe(
            { line: String? -> handleResponseLine(client, sid, line) },
            { error: Throwable ->
                ConsoleUtils.printErrorLog(error)
                client.sendEvent(
                    "[AI:CHAT:CREATE:MESSAGE]",
                    HandUtils.handleResultByCode(HttpStatus.OK, object : HashMap<String?, Any?>() {
                        init {
                            put("event", "PUSH-STREAM")
                            put("eventId", sid)
                            put("content", "请求失败，请查看日志")
                        }
                    }, "请求成功")
                )
            }, {
                ConsoleUtils.printInfoLog("AI Request Complete $sid")
            }
        )
    }

    private fun handleResponseLine(client: SocketIOClient, sid: String, line: String?) {
        if (line == null || line.trim { it <= ' ' }.isEmpty()) return
        if ("[DONE]" == line.trim { it <= ' ' }) return
        try {
            val contentNode = objectMapper.readTree(line).path("choices")[0].path("delta").path("content")
            if (contentNode.isMissingNode || contentNode.asText().isEmpty()) return
            val eventData: MutableMap<String, Any> = HashMap()
            eventData["event"] = "PUSH-STREAM"
            eventData["eventId"] = sid
            eventData["content"] = contentNode.asText()
            sendExecutor.execute {
                client.sendEvent("[AI:CHAT:CREATE:MESSAGE]", HandUtils.handleResultByCode(HttpStatus.OK, eventData, "请求成功"))
            }
        } catch (e: Exception) {
            ConsoleUtils.printErrorLog(e)
        }
    }

    companion object {
        private const val REQUEST_STATUS = "正在请求中"
        private val userMessageHistory: MutableMap<Long, MutableList<String?>> = ConcurrentHashMap()
        private const val DEFAULT_MESSAGE = "暂无AI能力相关权限，请前往任意非AI频道发送【handsock apply-ai】以开启AI能力"
    }
}