package com.example.common_network_error

sealed class NetworkError {
    object InvalidData : NetworkError()
    object Forbidden : NetworkError()
    object NotFound : NetworkError()
    object Unauthorized : NetworkError()
    object NoInternet : NetworkError()
    object ServerError : NetworkError()
    object Conflict : NetworkError()
    data class Unknown(val originalCode: Int) : NetworkError()
}
