package com.example.auth.data

import com.example.auth.data.model.AuthRequest
import com.example.auth.data.model.RegistrationRequest
import com.example.core_network.TokenResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("v1/auth/login")
    suspend fun login(@Body authRequest: AuthRequest) : TokenResponse

    @POST("v1/auth/register")
    suspend fun register(@Body authRequest: AuthRequest) : TokenResponse
}
