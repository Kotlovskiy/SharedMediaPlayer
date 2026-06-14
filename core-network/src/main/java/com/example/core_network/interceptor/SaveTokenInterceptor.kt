package com.example.core_network.interceptor

import com.example.core_network.dto.TokenResponse
import com.example.core_network.dto.toToken
import com.example.storage.TokenPreferences
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class SaveTokenInterceptor @Inject constructor(
    private val tokenPreferences: TokenPreferences,
    private val json: Json
) : Interceptor {
    @Synchronized
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        if (response.isSuccessful) {
            val body = response.body.string()
            val tokenResponse = json.decodeFromString<TokenResponse>(body)
            tokenPreferences.setNewToken(tokenResponse.toToken())
        }

        return response
    }
}
