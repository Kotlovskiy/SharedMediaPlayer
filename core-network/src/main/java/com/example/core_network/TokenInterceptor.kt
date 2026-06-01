package com.example.core_network

import com.example.token_manager_api.TokenProvider
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class TokenInterceptor @Inject constructor(
    private val tokenProvider: TokenProvider
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val request = originalRequest.newBuilder()
            .header("Authorization", "Bearer ${tokenProvider.getAccessToken()}")
            .build()

        return chain.proceed(request)
    }
}
