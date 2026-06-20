package com.example.core_network.interceptor

import com.example.core_network.AuthManager
import com.example.core_network.NetworkConstants
import com.example.core_network.qualifier.AuthorizedOkHttpClient
import kotlinx.serialization.json.Json
import okhttp3.Authenticator
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

class TokenAuthenticator @Inject constructor(
    private val authManager: AuthManager,
    private val json: Json,
    @param:AuthorizedOkHttpClient private val client: OkHttpClient
) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        val newToken = getFreshToken(response.request)
        return if (newToken != null) {
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
        val accessToken = authManager.getAccessToken() ?: return null
        val currentAccessToken = request.header(NetworkConstants.AUTHORIZATION_HEADER)
        if ("${NetworkConstants.BEARER} $accessToken" != currentAccessToken) {
            return accessToken
        }

        var newToken: String? = null
        authManager.refreshAccessToken(
            onSuccess = { newToken = it },
            onError = { newToken = null }
        )

        return newToken
    }
}
