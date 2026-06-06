package com.example.storage

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object StorageModule {
    @Provides
    fun provideCryptoManager(): CryptoManager {
        return CryptoManager()
    }

    @Provides
    @Singleton
    fun provideTokenPreferences(
        @ApplicationContext context: Context,
        cryptoManager: CryptoManager
    ): TokenPreferences {
        return TokenPreferences(context = context, cryptoManager = cryptoManager)
    }

    @Provides
    @Singleton
    fun provideAppDataStore(
        @ApplicationContext context: Context
    ): AppDataStore {
        return AppDataStore(context)
    }
}
