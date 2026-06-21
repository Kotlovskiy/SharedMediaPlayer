package com.example.room.data

import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import java.util.concurrent.ConcurrentSkipListSet
import javax.inject.Inject

class WssDataStreamCollector @Inject constructor() : WebSocketListener() {
    private val wssData = ConcurrentSkipListSet<ByteString>()

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        // Добавляем полученный бинарный фрагмент в хранилище
        wssData.add(bytes)
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosing(webSocket, code, reason)
        wssData.clear() // Очищаем буфер при закрытии соединения
    }

    // Проверяет, есть ли данные для чтения
    fun canStream(): Boolean {
        return wssData.isNotEmpty()
    }

    // Возвращает и удаляет первый доступный фрагмент из хранилища
    fun getNextStream(): ByteString {
        return wssData.pollFirst()
    }
}
