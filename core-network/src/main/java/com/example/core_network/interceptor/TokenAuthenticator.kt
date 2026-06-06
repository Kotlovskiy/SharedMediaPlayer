package com.example.core_network.interceptor

import com.example.core_network.NetworkConstants
import com.example.core_network.NetworkConstants.APPLICATION_JSON
import com.example.core_network.NetworkConstants.BASE_URL
import com.example.core_network.dto.TokenResponse
import com.example.core_network.dto.RefreshTokenRequest
import com.example.core_network.qualifier.AuthorizedOkHttpClient
import com.example.storage.TokenPreferences
import kotlinx.serialization.json.Json
import okhttp3.Authenticator
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

class TokenAuthenticator @Inject constructor(
    private val tokenPreferences: TokenPreferences,
    private val json: Json,
    @AuthorizedOkHttpClient private val client: OkHttpClient
) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        val newToken = getFreshToken(response.request)
        return if(newToken != null) {
            response.request.newBuilder()
                .header(
                    NetworkConstants.AUTHORIZATION_HEADER,
                    "${NetworkConstants.BEARER} $newToken"
                )
                .build()
        } else {
            null
        }
    }

    @Synchronized
    private fun getFreshToken(request: Request): String? {
        val accessToken = tokenPreferences.getAccessToken() ?: return null
        val refreshToken = tokenPreferences.getRefreshToken() ?: return null
        val currentAccessToken = request.header(NetworkConstants.AUTHORIZATION_HEADER)
        if ("${NetworkConstants.BEARER} $accessToken" != currentAccessToken) {
            return accessToken
        }

        val newToken = try {
            refreshToken(refreshToken)?.also {
                tokenPreferences.setNewToken(it)
            }
        } catch (_: Exception) {
            null
        }

        return newToken?.accessToken
    }

    private fun refreshToken(refreshToken: String) : TokenPreferences.Token? {
        val body = RefreshTokenRequest(refreshToken)
        val request = Request.Builder()
            .url("${BASE_URL}v1/auth/refresh")
            .post(
                json
                    .encodeToString(body)
                    .toRequestBody(APPLICATION_JSON.toMediaType())
            )
            .build()
        return client.newCall(request).execute().use { response ->
            if (response.isSuccessful) {
                val responseBody = response.body.string()
                val tokenResponse = json.decodeFromString<TokenResponse>(responseBody)
                TokenPreferences.Token(
                    accessToken = tokenResponse.accessToken,
                    refreshToken = tokenResponse.refreshToken,
                    accessTokenExpiredAt = tokenResponse.accessTokenExpiredAt,
                    refreshTokenExpiredAt = tokenResponse.refreshTokenExpiredAt
                )
            } else {
                null
            }
        }
    }
}