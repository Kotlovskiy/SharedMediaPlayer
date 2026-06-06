package com.example.auth.data

import com.example.core_network.dto.AuthRequest
import com.example.core_network.service.AuthService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AuthDataSource @Inject constructor(private val authService: AuthService) {
    suspend fun login(authRequest: AuthRequest) {
        return withContext(Dispatchers.IO) {
            authService.login(authRequest)
        }
    }

    suspend fun register(authRequest: AuthRequest) {
        return withContext(Dispatchers.IO) {
            authService.register(authRequest)
        }
    }
}