package com.example.auth

import com.example.auth.data.AuthDataSource
import com.example.core_network.service.AuthService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object AuthModule {
    @Provides
    fun provideAuthDataSource(api: AuthService): AuthDataSource {
        return AuthDataSource(api)
    }
}
