package com.example.kotlin_manager_impl

import com.example.storage.AppDataStore
import com.example.token_manager_api.TokenProvider
import com.example.token_manager_api.TokenRefresher
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TokenManagerModule {
    @Provides
    @Singleton
    fun provideTokenManager(storage: AppDataStore): TokenManager {
        return TokenManager(storage)
    }

    @Provides
    fun provideTokenProvider(tokenManager: TokenManager): TokenProvider {
        return tokenManager
    }

    @Provides
    fun provideTokenRefresher(tokenManager: TokenManager): TokenRefresher {
        return tokenManager
    }
}
