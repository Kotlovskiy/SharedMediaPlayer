package com.example.core_network

sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class HttpError<T>(val code: Int, val message: String, val body: String?) : ApiResult<T>()
    data class NetworkException(val error: Throwable) : ApiResult<Nothing>()
}
