package com.example.room.data

import com.example.common_network_error.NetworkError
import com.example.common_network_error.toNetworkError
import com.example.core_network.ApiResult
import com.example.core_network.dto.RoomResponse
import com.example.core_network.dto.queue.QueueResponse
import com.example.core_network.dto.queue.TrackResponse
import com.example.room.domain.Result
import com.example.room.domain.Room
import com.example.room.domain.Song
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RoomRepository @Inject constructor(
    private val roomDataSource: RoomDataSource
) {
    suspend fun getRoom(roomId: String): Result<Room> =
        when(val result = roomDataSource.getRoom(roomId)) {
            is ApiResult.HttpError<*> -> {
                Result.Error(result.code.toNetworkError())
            }
            is ApiResult.NetworkException ->
                Result.Error(NetworkError.NoInternet)
            is ApiResult.Success<RoomResponse> ->
                Result.Success(result.data.toRoom())
        }

    suspend fun getQueue(roomId: String): Result<List<Song>> =
        when(val result = roomDataSource.getQueue(roomId)) {
            is ApiResult.HttpError<*> -> {
                Result.Error(result.code.toNetworkError())
            }
            is ApiResult.NetworkException ->
                Result.Error(NetworkError.NoInternet)
            is ApiResult.Success<QueueResponse> ->
                Result.Success(result.data.tracks.toSongs())
        }

    suspend fun addSong(roomId: String, url: String): Result<Song> =
        when(val result = roomDataSource.addSong(roomId, url)) {
            is ApiResult.HttpError<*> -> {
                Result.Error(result.code.toNetworkError())
            }
            is ApiResult.NetworkException ->
                Result.Error(NetworkError.NoInternet)
            is ApiResult.Success<TrackResponse> ->
                Result.Success(result.data.toSong())
        }

    suspend fun moveSong(roomId: String, trackId: String, newPos: Int): Result<Song> =
        when(val result = roomDataSource.moveSong(roomId, trackId, newPos)) {
            is ApiResult.HttpError<*> -> {
                Result.Error(result.code.toNetworkError())
            }
            is ApiResult.NetworkException ->
                Result.Error(NetworkError.NoInternet)
            is ApiResult.Success<TrackResponse> ->
                Result.Success(result.data.toSong())
        }

    suspend fun deleteSong(roomId: String, trackId: String): Result<Unit> =
        when(val result = roomDataSource.deleteSong(roomId, trackId)) {
            is ApiResult.HttpError<*> -> {
                Result.Error(result.code.toNetworkError())
            }
            is ApiResult.NetworkException ->
                Result.Error(NetworkError.NoInternet)
            is ApiResult.Success<*> ->
                Result.Success(Unit)
        }

    suspend fun deleteAllSong(roomId: String): Result<Unit> =
        when(val result = roomDataSource.deleteAllSongs(roomId)) {
            is ApiResult.HttpError<*> -> {
                Result.Error(result.code.toNetworkError())
            }
            is ApiResult.NetworkException ->
                Result.Error(NetworkError.NoInternet)
            is ApiResult.Success<*> ->
                Result.Success(Unit)
        }

    fun observeTrackChanges(roomId: String): Flow<Result<TrackResponse>> =
        roomDataSource.observeTrackChanges(roomId)
            .map {
                if (it.isSuccess) {
                    val track = it.getOrNull()
                    if(track != null) {
                        Result.Success(track)
                    } else {
                        Result.Error(NetworkError.Unknown(-1))
                    }
                } else {
                    Result.Error(NetworkError.Unknown(-1))
                }
            }

    fun observePlaybackSession(roomId: String) = roomDataSource.observePlaybackSession(roomId)
}
