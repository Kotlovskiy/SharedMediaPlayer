package com.example.token_manager_api

interface TokenRefresher {
    suspend fun refreshToken(token: Token?)
}
