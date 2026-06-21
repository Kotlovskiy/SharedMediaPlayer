package com.example.core_network

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

class StompClient(
    private val okHttpClient: OkHttpClient,
    val json: Json
) {
    private var webSocket: WebSocket? = null
    private var sessionId: String? = null
    private val _messages = MutableSharedFlow<String>(extraBufferCapacity = 64)
    val messages: SharedFlow<String> = _messages.asSharedFlow()

    private val subscriptionMap = mutableMapOf<String, Channel<String>>()
    private var isConnected = false
    val connected
        get() = isConnected

    suspend fun connect(webSocketUrl: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url(webSocketUrl)
                .build()

            val connectionDeferred = CompletableDeferred<Unit>()

            webSocket = okHttpClient.newWebSocket(request, object : WebSocketListener() {
                override fun onOpen(webSocket: WebSocket, response: Response) {
                    println("WebSocket opened to $webSocketUrl")
                    sendStompFrame(StompFrame.CONNECT, mapOf(
                        "accept-version" to "1.2,1.1",
                        "heart-beat" to "10000,10000"
                    ))
                }

                override fun onMessage(webSocket: WebSocket, text: String) {
                    handleStompMessage(text)
                }

                override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                    println("WebSocket closed: $reason (code $code)")
                    isConnected = false
                    if (!connectionDeferred.isCompleted) {
                        connectionDeferred.completeExceptionally(Exception("Connection closed: $reason"))
                    }
                }

                override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                    println("WebSocket failure: ${t.message}")
                    isConnected = false
                    if (!connectionDeferred.isCompleted) {
                        connectionDeferred.completeExceptionally(t)
                    }
                }
            })

            withTimeoutOrNull(5000L) {
                connectionDeferred.await()
            } ?: throw Exception("Connection timeout")

            isConnected = true
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun subscribe(destination: String): Flow<String> = callbackFlow {
        val subscriptionId = "sub-${System.currentTimeMillis()}"
        val channel = Channel<String>(Channel.BUFFERED)
        subscriptionMap[subscriptionId] = channel

        sendStompFrame(StompFrame.SUBSCRIBE, mapOf(
            "id" to subscriptionId,
            "destination" to destination
        ))

        try {
            for (message in channel) {
                send(message)
            }
        } finally {
            sendStompFrame(StompFrame.UNSUBSCRIBE, mapOf("id" to subscriptionId))
            subscriptionMap.remove(subscriptionId)
            channel.close()
        }
    }.flowOn(Dispatchers.IO)

    suspend inline fun <reified T> subscribeDeserialized(destination: String): Flow<T> {
        return subscribe(destination)
            .map { raw ->
                json.decodeFromString<T>(raw)
            }
    }

    private fun sendStompFrame(command: String, headers: Map<String, String> = emptyMap(), body: String = "") {
        if (webSocket == null) {
            println("WebSocket is null, cannot send $command")
            return
        }
        val frame = buildString {
            append(command)
            append("\n")
            headers.forEach { (key, value) ->
                append("$key:$value\n")
            }
            append("\n")
            append(body)
            append("\u0000")
        }
        webSocket?.send(frame)
    }

    private fun handleStompMessage(text: String) {
        val lines = text.split("\n")
        if (lines.isEmpty()) return

        val command = lines[0].trim()

        when (command) {
            "CONNECTED" -> {
                sessionId = extractHeader(lines, "session")
                isConnected = true
                println("STOMP connected, session=$sessionId")
            }
            "MESSAGE" -> {
                val destination = extractHeader(lines, "destination")
                val subscriptionId = extractHeader(lines, "subscription")
                val body = extractBody(text)

                runBlocking { _messages.emit(body ?: "") }

                subscriptionMap[subscriptionId]?.trySend(body ?: "")
            }
            "RECEIPT" -> {}
            "ERROR" -> {
                println("STOMP error: $text")
            }
        }
    }

    private fun extractHeader(lines: List<String>, key: String): String? {
        return lines.firstOrNull { it.startsWith("$key:") }
            ?.substringAfter("$key:")
            ?.trim()
    }

    private fun extractBody(text: String): String? {
        val parts = text.split("\n\n")
        return if (parts.size > 1) {
            parts[1].trimEnd('\u0000')
        } else null
    }

    fun disconnect() {
        sendStompFrame(StompFrame.DISCONNECT)
        webSocket?.close(1000, "Normal disconnect")
        webSocket = null
        isConnected = false
        subscriptionMap.values.forEach { it.close() }
        subscriptionMap.clear()
    }

    object StompFrame {
        const val CONNECT = "CONNECT"
        const val CONNECTED = "CONNECTED"
        const val SUBSCRIBE = "SUBSCRIBE"
        const val UNSUBSCRIBE = "UNSUBSCRIBE"
        const val MESSAGE = "MESSAGE"
        const val DISCONNECT = "DISCONNECT"
        const val ERROR = "ERROR"
        const val RECEIPT = "RECEIPT"
    }
}
