package com.example.core_network

import com.example.token_manager_api.Token
import kotlinx.serialization.Serializable

@Serializable
data class TokenResponse(
    val accessToken: String,
    val accessTokenExpiredAt: String,
    val refreshToken: String,
    val refreshTokenExpiredAt: String,
)

fun TokenResponse.toToken() : Token {
    return Token(
        accessToken = this.accessToken,
        refreshToken = this.refreshToken,
        accessTokenExpiredAt = this.accessTokenExpiredAt,
        refreshTokenExpiredAt = this.refreshTokenExpiredAt
    )
}
