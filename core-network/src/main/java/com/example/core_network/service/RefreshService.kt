package com.example.core_network.service

import retrofit2.http.Body
import retrofit2.http.POST

interface RefreshService {
    @POST("refresh")
    suspend fun refresh(@Body refreshToken: String)
}
