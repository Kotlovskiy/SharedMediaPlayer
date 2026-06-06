package com.example.core_network.service

import com.example.core_network.dto.AuthRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("v1/auth/login")
    suspend fun login(@Body authRequest: AuthRequest)

    @POST("v1/auth/register")
    suspend fun register(@Body authRequest: AuthRequest)
}