package com.example.core_network.interceptor

import android.util.Log
import com.example.core_network.AuthManager
import com.example.core_network.NetworkConstants
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

class TokenAuthenticator @Inject constructor(
    private val authManager: AuthManager
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

        return try {
            runBlocking {
                withTimeoutOrNull(10_000L) {
                    authManager.refreshAccessToken()
                }
            }
        } catch (e: Exception) {
            Log.e("TokenAuthenticator", "Token refresh failed", e)
            null
        }
    }
}
