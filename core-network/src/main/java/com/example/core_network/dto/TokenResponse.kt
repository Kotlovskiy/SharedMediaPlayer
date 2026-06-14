package com.example.core_network.dto

import com.example.storage.TokenPreferences.Token
import kotlinx.serialization.Serializable

@Serializable
data class TokenResponse(
    val accessToken: String,
    val accessTokenExpiredAt: String,
    val refreshToken: String,
    val refreshTokenExpiredAt: String,
)

fun TokenResponse.toToken(): Token {
    return Token(
        accessToken = this.accessToken,
        refreshToken = this.refreshToken,
        accessTokenExpiredAt = this.accessTokenExpiredAt,
        refreshTokenExpiredAt = this.refreshTokenExpiredAt
    )
}
