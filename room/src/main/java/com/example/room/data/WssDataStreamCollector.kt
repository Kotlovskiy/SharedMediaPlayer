package com.example.room.data

import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import java.util.concurrent.ConcurrentSkipListSet
import javax.inject.Inject

class WssDataStreamCollector @Inject constructor() : WebSocketListener() {
    private val wssData = ConcurrentSkipListSet<ByteString>()

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        wssData.add(bytes)
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosing(webSocket, code, reason)
        wssData.clear()
    }

    fun canStream(): Boolean {
        return wssData.isNotEmpty()
    }

    fun getNextStream(): ByteString {
        return wssData.pollFirst()
    }
}
