package com.example.room.data

import com.example.core_network.ApiResult
import com.example.core_network.StompClient
import com.example.core_network.dto.queue.MoveTrackRequest
import com.example.core_network.dto.queue.TrackResponse
import com.example.core_network.service.QueueService
import com.example.core_network.service.RoomService
import com.example.core_network.toApiResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject

class RoomDataSource @Inject constructor(
    private val roomApi: RoomService,
    private val queueApi: QueueService,
    private val stompClient: StompClient
) {
    private val connectMutex = Mutex()
    @Volatile
    private var connectionAttempts = 0
    private val maxRetryAttempts = 1
    suspend fun getRoom(roomId: String) = withContext(Dispatchers.IO) {
        try {
            roomApi.getRoom(roomId).toApiResult()
        } catch (e: IOException) {
            ApiResult.NetworkException(e)
        }
    }

    suspend fun addSong(
        roomId: String,
        url: String
    ) = withContext(Dispatchers.IO) {
        try {
            queueApi.addSong(
                roomId = roomId,
                youtubeUrl = url
            ).toApiResult()
        } catch (e: IOException) {
            ApiResult.NetworkException(e)
        }
    }

    suspend fun getQueue(roomId: String) = withContext(Dispatchers.IO) {
        try {
            queueApi.getQueue(roomId).toApiResult()
        } catch (e: IOException) {
            ApiResult.NetworkException(e)
        }
    }

    suspend fun getSong(roomId: String) = withContext(Dispatchers.IO) {
        try {
            queueApi.getSong(roomId).toApiResult()
        } catch (e: IOException) {
            ApiResult.NetworkException(e)
        }
    }

    suspend fun moveSong(
        roomId: String,
        trackId: String,
        newPosition: Int
    ) = withContext(Dispatchers.IO) {
        try {
            queueApi.moveSong(
                roomId = roomId,
                trackId = trackId,
                moveTrackRequest = MoveTrackRequest(newPosition)
            ).toApiResult()
        } catch (e: IOException) {
            ApiResult.NetworkException(e)
        }
    }

    suspend fun deleteSong(
        roomId: String,
        trackId: String
    ) = withContext(Dispatchers.IO) {
        try {
            queueApi.deleteSong(
                roomId = roomId,
                trackId = trackId
            ).toApiResult()
        } catch (e: IOException) {
            ApiResult.NetworkException(e)
        }
    }

    suspend fun deleteAllSongs(roomId: String) = withContext(Dispatchers.IO) {
        try {
            queueApi.deleteAllSongs(roomId).toApiResult()
        } catch (e: IOException) {
            ApiResult.NetworkException(e)
        }
    }

    private suspend fun connectToWebSocket(): Result<Unit> = connectMutex.withLock {
        if (stompClient.connected) {
            return Result.success(Unit)
        }

        if (connectionAttempts >= maxRetryAttempts) {
            return Result.failure(IllegalStateException("Max connection attempts reached"))
        }

        connectionAttempts++

        return try {
            stompClient.connect("ws://bore.pub:63890/ws/websocket")
            connectionAttempts = 0
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun observeTrackChanges(roomId: String): Flow<Result<TrackResponse>> = callbackFlow {
        val result = connectToWebSocket()
        if (result.isFailure) {
            close()
            return@callbackFlow
        }
        val destination = "/topic/room.$roomId.track.changed"
        stompClient.subscribeDeserialized<TrackResponse>(destination)
            .map { track ->
                try {
                    Result.success(track)
                } catch (e: Exception) {
                    Result.failure(e)
                }
            }
            .collect { result ->
                send(result)
            }

    }.flowOn(Dispatchers.IO)

    fun observePlaybackSession(roomId: String) = callbackFlow {
        val result = connectToWebSocket()
        if (result.isFailure) {
            close()
            return@callbackFlow
        }
        val destination = "/exchange/amq.topic/room.$roomId.session.started"
        stompClient.subscribe(destination)
            .collect { result ->
                send(result)
            }

    }.flowOn(Dispatchers.IO)
}
