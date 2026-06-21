package com.example.common_network_error

import com.example.common_network_error.NetworkError.Conflict
import com.example.common_network_error.NetworkError.Forbidden
import com.example.common_network_error.NetworkError.InvalidData
import com.example.common_network_error.NetworkError.NotFound
import com.example.common_network_error.NetworkError.ServerError
import com.example.common_network_error.NetworkError.Unauthorized
import com.example.common_network_error.NetworkError.Unknown

fun Int.toNetworkError(): NetworkError =
    when(this) {
        400 -> InvalidData
        401 -> Unauthorized
        403 -> Forbidden
        404 -> NotFound
        409 -> Conflict
        in 500..599 -> ServerError
        else -> Unknown(this)
    }
