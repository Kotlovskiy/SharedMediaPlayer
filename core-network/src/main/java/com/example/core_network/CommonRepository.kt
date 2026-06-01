package com.example.core_network

import com.example.token_manager_api.TokenProvider
import com.example.token_manager_api.TokenRefresher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import javax.inject.Inject

class CommonRepository @Inject constructor(
    private val tokenDataSource: TokenDataSource,
    private val tokenRefresher: TokenRefresher,
    private val tokenProvider: TokenProvider
) {
    suspend fun <T> execute(block: suspend () -> T) : T {
        return withContext(Dispatchers.IO) {
            try {
                block()
            } catch (e: HttpException) {
                if(e.code() == 401 && tokenProvider.isRefreshTokenValid()) {
                    val newToken = tokenDataSource.refreshToken()
                    tokenRefresher.refreshToken(newToken?.toToken())
                    block()
                } else {
                    throw e
                }
            }
        }
    }

    suspend fun logout() {
        withContext(Dispatchers.IO) {
            tokenDataSource.logout()
            tokenRefresher.refreshToken(null)
        }
    }
}
