package com.example.core_network.service

import com.example.core_network.dto.AuthRequest
import com.example.core_network.dto.RegistrationRequest
import com.example.core_network.dto.TokenResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("v1/auth/login")
    suspend fun login(@Body authRequest: AuthRequest) : Response<TokenResponse>

    @POST("v1/auth/register")
    suspend fun register(@Body registrationRequest: RegistrationRequest) : Response<TokenResponse>
}
