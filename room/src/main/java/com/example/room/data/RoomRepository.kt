package com.example.room.data

import com.example.common_network_error.NetworkError
import com.example.core_network.ApiResult
import com.example.core_network.dto.RoomResponse
import com.example.room.domain.Result
import com.example.room.domain.Room
import javax.inject.Inject

class RoomRepository @Inject constructor(
    private val roomDataSource: RoomDataSource
) {
    suspend fun getRoom(roomId: String): Result<Room> =
        when(val result = roomDataSource.getRoom(roomId)) {
            is ApiResult.HttpError<*> -> {
                val networkError = when (result.code) {
                    401 -> NetworkError.Unauthorized
                    403 -> NetworkError.Forbidden
                    404 -> NetworkError.NotFound
                    in 500..599 -> NetworkError.ServerError
                    else -> NetworkError.Unknown(result.code)
                }
                Result.Error(networkError)
            }
            is ApiResult.NetworkException ->
                Result.Error(NetworkError.NoInternet)
            is ApiResult.Success<RoomResponse> ->
                Result.Success(result.data.toRoom())
        }
}
