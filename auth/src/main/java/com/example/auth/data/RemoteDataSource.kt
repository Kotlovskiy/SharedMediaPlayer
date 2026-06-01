package com.example.auth.data

import com.example.auth.data.model.AuthRequest
import com.example.auth.data.model.RegistrationRequest
import com.example.core_network.TokenResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RemoteDataSource @Inject constructor(private val authService: AuthService) {
    suspend fun login(authRequest: AuthRequest) : TokenResponse {
        return withContext(Dispatchers.IO) {
            authService.login(authRequest)
        }
    }

    suspend fun register(authRequest: AuthRequest) : TokenResponse {
        return withContext(Dispatchers.IO) {
            authService.register(authRequest)
        }
    }
}