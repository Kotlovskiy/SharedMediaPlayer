package com.example.room.data

import android.net.Uri
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.BaseDataSource
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DataSpec
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import androidx.core.net.toUri
import androidx.media3.common.C

@UnstableApi
class WssStreamDataSource(
    private val okHttpClient: OkHttpClient,
    private val dataStreamCollector: WssDataStreamCollector
) : BaseDataSource(true) {

    private var webSocket: WebSocket? = null
    private var currentByteStream: ByteArray? = null
    private var currentPosition = 0
    private var remainingBytes = 0

    override fun open(p0: DataSpec): Long {
        val request = Request.Builder()
            .url(p0.uri.toString())
            .build()

        webSocket = okHttpClient.newWebSocket(request, dataStreamCollector)

        return C.LENGTH_UNSET.toLong()
    }

    override fun read(target: ByteArray, offset: Int, length: Int): Int {
        if (currentByteStream == null && dataStreamCollector.canStream()) {
            currentByteStream = dataStreamCollector.getNextStream().toByteArray()
            currentPosition = 0
            remainingBytes = currentByteStream?.size ?: 0
        }

        if (currentByteStream == null) {
            return 0
        }

        val readSize = minOf(length, remainingBytes)
        currentByteStream?.copyInto(
            target,
            offset,
            currentPosition,
            currentPosition + readSize
        )

        currentPosition += readSize
        remainingBytes -= readSize

        if (remainingBytes == 0) {
            currentByteStream = null
        }

        return readSize
    }

    override fun getUri(): Uri? {
        return webSocket?.request()?.url?.toString()?.toUri()
    }

    override fun close() {
        webSocket?.cancel()
        currentByteStream = null
        remainingBytes = 0
        while (dataStreamCollector.canStream()) {
            dataStreamCollector.getNextStream()
        }
    }

    class Factory(
        private val okHttpClient: OkHttpClient,
        private val dataStreamCollector: WssDataStreamCollector
    ) : DataSource.Factory {
        override fun createDataSource(): DataSource {
            return WssStreamDataSource(okHttpClient, dataStreamCollector)
        }
    }
}
