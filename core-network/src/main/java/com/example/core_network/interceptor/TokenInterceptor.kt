package com.example.core_network.interceptor

import com.example.core_network.NetworkConstants.AUTHORIZATION_HEADER
import com.example.core_network.NetworkConstants.BEARER
import com.example.storage.TokenPreferences
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class TokenInterceptor @Inject constructor(
    private val tokenPreferences: TokenPreferences
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val request = originalRequest.newBuilder()
            .header(
                AUTHORIZATION_HEADER,
                "$BEARER ${tokenPreferences.getAccessToken()}"
            )
            .build()

        return chain.proceed(request)
    }
}
