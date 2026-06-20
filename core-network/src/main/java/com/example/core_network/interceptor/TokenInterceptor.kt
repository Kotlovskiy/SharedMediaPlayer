package com.example.core_network.interceptor

import com.example.core_network.AuthManager
import com.example.core_network.NetworkConstants.AUTHORIZATION_HEADER
import com.example.core_network.NetworkConstants.BEARER
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class TokenInterceptor @Inject constructor(
    private val authManager: AuthManager
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val request = originalRequest.newBuilder()
            .header(
                AUTHORIZATION_HEADER,
                "$BEARER ${authManager.getAccessToken()}"
            )
            .build()

        return chain.proceed(request)
    }
}
