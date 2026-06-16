package com.example.hello.data

import com.example.common_network_error.NetworkError
import com.example.core_network.ApiResult
import com.example.core_network.dto.RoomResponse
import com.example.hello.domain.Result
import com.example.hello.domain.Room
import com.example.hello.domain.toRoom
import javax.inject.Inject

class InviteRepository @Inject constructor(
    private val dataSource: InviteDataSource
) {
    suspend fun createRoom(roomName: String): Result<Room> {
        return when(val result = dataSource.createRoom(roomName)) {
            is ApiResult.HttpError<*> -> {
                val networkError = when (result.code) {
                    400 -> NetworkError.InvalidData
                    401 -> NetworkError.Unauthorized
                    409 -> NetworkError.Conflict
                    in 500..599 -> NetworkError.ServerError
                    else -> NetworkError.Unknown(result.code)
                }
                Result.Error(networkError)
            }
            is ApiResult.NetworkException ->
                Result.Error(NetworkError.NoInternet)
            is ApiResult.Success<RoomResponse> -> Result.Success(result.data.toRoom())
        }
    }

    suspend fun joinRoom(inviteCode: String): Result<Room> {
        return when(val result = dataSource.joinRoom(inviteCode)) {
            is ApiResult.HttpError<*> -> {
                val networkError = when (result.code) {
                    400 -> NetworkError.InvalidData
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
            is ApiResult.Success<RoomResponse> -> Result.Success(result.data.toRoom())
        }
    }
}
