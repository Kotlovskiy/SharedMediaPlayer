package com.example.auth.data

import com.example.common_network_error.NetworkError
import com.example.core_network.ApiResult
import com.example.auth.domain.Result as AuthResult
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val authDataSource: AuthDataSource
) {
    suspend fun login(
        email: String,
        password: String
    ): AuthResult<Unit> {
        return when(val result = authDataSource.login(
            email = email,
            password = password
        )) {
            is ApiResult.HttpError<*> -> {
                val networkError = when (result.code) {
                    400 -> NetworkError.InvalidData
                    403 -> NetworkError.Forbidden
                    in 500..599 -> NetworkError.ServerError
                    else -> NetworkError.Unknown(result.code)
                }
                AuthResult.Error(networkError)
            }
            is ApiResult.NetworkException ->
                AuthResult.Error(NetworkError.NoInternet)
            is ApiResult.Success<*> ->
                AuthResult.Success(Unit)
        }
    }

    suspend fun register(
        nickname: String,
        email: String,
        password: String
    ): AuthResult<Unit> {
        return when(val result = authDataSource.register(
            nickname = nickname,
            email = email,
            password = password
        )) {
            is ApiResult.HttpError<*> -> {
                val networkError = when (result.code) {
                    400 -> NetworkError.InvalidData
                    403 -> NetworkError.Forbidden
                    409 -> NetworkError.Conflict
                    in 500..599 -> NetworkError.ServerError
                    else -> NetworkError.Unknown(result.code)
                }
                AuthResult.Error(networkError)
            }
            is ApiResult.NetworkException ->
                AuthResult.Error(NetworkError.NoInternet)
            is ApiResult.Success<*> ->
                AuthResult.Success(Unit)
        }
    }
}
