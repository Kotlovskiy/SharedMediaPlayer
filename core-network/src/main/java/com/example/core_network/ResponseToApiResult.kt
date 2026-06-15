package com.example.core_network

import retrofit2.Response
import java.io.IOException

fun <T> Response<T>.toApiResult(): ApiResult<T> {
    return if (isSuccessful) {
        body()?.let { responseBody ->
            ApiResult.Success(responseBody)
        } ?: ApiResult.HttpError(
            code = code(),
            message = message() ?: "",
            body = null
        )
    } else {
        ApiResult.HttpError(
            code = code(),
            message = message(),
            body = errorBody()?.string()
        )
    }
}

fun Response<Unit>.toUnitApiResult(): ApiResult<Unit> {
    return try {
        if (isSuccessful) {
            ApiResult.Success(Unit)
        } else {
            ApiResult.HttpError(
                code = code(),
                message = message(),
                body = errorBody()?.string()
            )
        }
    } catch (e: IOException) {
        ApiResult.NetworkException(e)
    }
}
