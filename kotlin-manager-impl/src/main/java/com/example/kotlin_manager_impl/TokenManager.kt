package com.example.kotlin_manager_impl

import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.storage.AppDataStore
import com.example.token_manager_api.Token
import com.example.token_manager_api.TokenProvider
import com.example.token_manager_api.TokenRefresher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.time.OffsetDateTime
import javax.inject.Inject

class TokenManager @Inject constructor(
    private val storage: AppDataStore
) : TokenRefresher, TokenProvider {

    private var cachedAccessToken: String? = null
    private var cachedRefreshToken: String? = null
    private var cachedAccessTokenExpiredAt: String? = null
    private var cachedRefreshTokenExpiredAt: String? = null

    init {
        CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
            cachedAccessToken = storage.getString(ACCESS_TOKEN_KEY)
            cachedRefreshToken = storage.getString(REFRESH_TOKEN_KEY)
            cachedAccessTokenExpiredAt = storage.getString(ACCESS_TOKEN_EXPIRED_AT_KEY)
            cachedRefreshTokenExpiredAt = storage.getString(REFRESH_TOKEN_EXPIRED_AT_KEY)
        }
    }

    override suspend fun refreshToken(token: Token?) {
        storage.setString(key = ACCESS_TOKEN_KEY, value = token?.accessToken ?: "")
        storage.setString(key = REFRESH_TOKEN_KEY, value = token?.refreshToken ?: "")
        storage.setString(key = ACCESS_TOKEN_EXPIRED_AT_KEY, value = token?.accessTokenExpiredAt ?: "")
        storage.setString(key = REFRESH_TOKEN_EXPIRED_AT_KEY, value = token?.refreshTokenExpiredAt ?: "")
        cachedAccessToken = token?.accessToken
        cachedRefreshToken = token?.refreshToken
        cachedAccessTokenExpiredAt = token?.accessTokenExpiredAt
        cachedRefreshTokenExpiredAt = token?.refreshTokenExpiredAt
    }

    override fun getAccessToken(): String? {
        return cachedAccessToken
    }

    override fun getRefreshToken(): String? {
        return cachedRefreshToken
    }

    override fun isAccessTokenValid(): Boolean {
        if (cachedAccessTokenExpiredAt == null) return false
        return OffsetDateTime.now() < OffsetDateTime.parse(cachedAccessTokenExpiredAt)
    }

    override fun isRefreshTokenValid(): Boolean {
        if (cachedRefreshTokenExpiredAt == null) return false
        return OffsetDateTime.now() < OffsetDateTime.parse(cachedRefreshTokenExpiredAt)
    }


    companion object {
        private val ACCESS_TOKEN_KEY = stringPreferencesKey("ACCESS_TOKEN_KEY")
        private val REFRESH_TOKEN_KEY = stringPreferencesKey("REFRESH_TOKEN_KEY")
        private val ACCESS_TOKEN_EXPIRED_AT_KEY = stringPreferencesKey("ACCESS_TOKEN_EXPIRED_AT_KEY")
        private val REFRESH_TOKEN_EXPIRED_AT_KEY = stringPreferencesKey("REFRESH_TOKEN_EXPIRED_AT_KEY")
    }
}
