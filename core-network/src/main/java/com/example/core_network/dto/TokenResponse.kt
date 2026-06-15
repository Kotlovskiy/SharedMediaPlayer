package com.example.core_network.dto

import com.example.storage.TokenPreferences.Token
import kotlinx.serialization.Serializable
import java.time.OffsetDateTime

@Serializable
data class TokenResponse(
    val access_token: String,
    val expires_in: Int,
    val refresh_token: String,
    val refresh_expires_in: Int
)

fun TokenResponse.toToken(): Token {
    val now = OffsetDateTime.now()

    return Token(
        accessToken = this.access_token,
        refreshToken = this.refresh_token,
        accessTokenExpiredAt = now.plusSeconds(this.expires_in.toLong()).toString(),
        refreshTokenExpiredAt = now.plusSeconds(this.refresh_expires_in.toLong()).toString()
    )
}
