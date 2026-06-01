package com.example.core_network

import com.example.token_manager_api.TokenProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TokenDataSource @Inject constructor(
    private val tokenService: TokenService,
    private val tokenProvider: TokenProvider
) {
    suspend fun refreshToken() : TokenResponse? {
        val refreshToken = tokenProvider.getRefreshToken()
        if (refreshToken != null) {
            return withContext(Dispatchers.IO) {
                tokenService.refresh(refreshToken = refreshToken)
            }
        }
        return null
    }

    suspend fun logout() {
        withContext(Dispatchers.IO) {
            tokenService.logout()
        }
    }
}