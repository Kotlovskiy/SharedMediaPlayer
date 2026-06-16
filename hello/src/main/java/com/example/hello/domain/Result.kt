package com.example.hello.domain

import com.example.common_network_error.NetworkError

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val error: NetworkError) : Result<Nothing>()
}
