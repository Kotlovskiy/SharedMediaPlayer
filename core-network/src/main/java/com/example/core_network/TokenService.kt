package com.example.core_network

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST

interface TokenService {
    @DELETE("logout")
    suspend fun logout()

    @POST("refresh")
    suspend fun refresh(@Body refreshToken: String): TokenResponse
}
