package com.example.storage

import android.content.Context
import androidx.core.content.edit
import java.time.OffsetDateTime
import java.time.ZonedDateTime
import javax.inject.Inject

class TokenPreferences @Inject constructor(
    context: Context,
    private val cryptoManager: CryptoManager
) {

    private val preferences = context.getSharedPreferences(
        PREFERENCES_NAME,
        Context.MODE_PRIVATE
    )

    fun setNewToken(token: Token) {
        preferences.edit {
            putString(
                ACCESS_TOKEN_KEY,
                cryptoManager.encryptBase64(token.accessToken)
            )
            putString(
                ACCESS_TOKEN_EXPIRATION_KEY,
                cryptoManager.encryptBase64(token.accessTokenExpiredAt)
            )
            putString(
                REFRESH_TOKEN_KEY,
                cryptoManager.encryptBase64(token.refreshToken)
            )
            putString(
                REFRESH_TOKEN_EXPIRATION_KEY,
                cryptoManager.encryptBase64(token.refreshTokenExpiredAt)
            )
        }
    }

    fun getAccessToken(): String? {
        return preferences.getString(
            ACCESS_TOKEN_KEY,
            null
        )?. let { cryptoManager.decryptBase64(it) }
    }

    fun getRefreshToken(): String? {
        return preferences.getString(
            REFRESH_TOKEN_KEY,
            null
        )?. let { cryptoManager.decryptBase64(it) }
    }

    fun isAccessTokenValid(): Boolean {
        val expiration = preferences.getString(
            ACCESS_TOKEN_EXPIRATION_KEY,
            null
        )?. let { cryptoManager.decryptBase64(it) }

        return expiration != null &&
            OffsetDateTime.now() < ZonedDateTime.parse(expiration).toOffsetDateTime()
    }

    fun isRefreshTokenValid(): Boolean {
        val expiration = preferences.getString(
            REFRESH_TOKEN_EXPIRATION_KEY,
            null
        )?. let { cryptoManager.decryptBase64(it) }

        return expiration != null &&
            OffsetDateTime.now() < ZonedDateTime.parse(expiration).toOffsetDateTime()
    }

    fun deleteToken() {
        preferences.edit {
            remove(ACCESS_TOKEN_KEY)
            remove(ACCESS_TOKEN_EXPIRATION_KEY)
            remove(REFRESH_TOKEN_KEY)
            remove(REFRESH_TOKEN_EXPIRATION_KEY)
        }
    }

    data class Token(
        val accessToken: String,
        val accessTokenExpiredAt: String,
        val refreshToken: String,
        val refreshTokenExpiredAt: String,
    )

    private companion object {
        const val PREFERENCES_NAME = "token_preferences"

        const val ACCESS_TOKEN_KEY = "access_token"
        const val ACCESS_TOKEN_EXPIRATION_KEY = "access_token_expired_at"
        const val REFRESH_TOKEN_KEY = "refresh_token"
        const val REFRESH_TOKEN_EXPIRATION_KEY = "refresh_token_expired_at"
    }
}
