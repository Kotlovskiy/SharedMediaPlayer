package com.example.core_network

import com.example.core_network.NetworkConstants.APPLICATION_JSON
import com.example.core_network.NetworkConstants.BASE_URL
import com.example.core_network.interceptor.SaveTokenInterceptor
import com.example.core_network.interceptor.TokenAuthenticator
import com.example.core_network.interceptor.TokenInterceptor
import com.example.core_network.qualifier.AuthorizedOkHttpClient
import com.example.core_network.qualifier.AuthorizedRetrofit
import com.example.core_network.qualifier.UnauthorizedOkHttpClient
import com.example.core_network.qualifier.UnauthorizedRetrofit
import com.example.core_network.service.AuthService
import com.example.core_network.service.LogoutService
import com.example.core_network.service.RefreshService
import com.example.core_network.service.RoomService
import com.example.storage.TokenPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC)
    }

    @Provides
    @Singleton
    fun provideTokenInterceptor(tokenPreferences: TokenPreferences): TokenInterceptor {
        return TokenInterceptor(tokenPreferences)
    }

    @Provides
    fun provideSaveTokenInterceptor(
        tokenPreferences: TokenPreferences,
        json: Json
    ): SaveTokenInterceptor {
        return SaveTokenInterceptor(tokenPreferences, json)
    }

    @Provides
    @Singleton
    fun provideTokenAuthenticator(
        tokenPreferences: TokenPreferences,
        json: Json,
        @UnauthorizedOkHttpClient okHttpClient: OkHttpClient
    ): TokenAuthenticator {
        return TokenAuthenticator(
            tokenPreferences = tokenPreferences,
            json = json,
            client = okHttpClient
        )
    }

    @UnauthorizedOkHttpClient
    @Provides
    @Singleton
    fun provideUnauthorizedOkHttpClient(
        saveTokenInterceptor: SaveTokenInterceptor,
        loggingInterceptor: HttpLoggingInterceptor,
    ): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(saveTokenInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()

    @AuthorizedOkHttpClient
    @Provides
    @Singleton
    fun provideAuthorizedOkHttpClient(
        tokenInterceptor: TokenInterceptor,
        loggingInterceptor: HttpLoggingInterceptor,
        authenticator: TokenAuthenticator
    ): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(tokenInterceptor)
            .addInterceptor(loggingInterceptor)
            .authenticator(authenticator)
            .build()

    @Provides
    @Singleton
    fun provideJson(): Json =
        Json {
            ignoreUnknownKeys = true
        }

    @AuthorizedRetrofit
    @Provides
    @Singleton
    fun provideAuthorizedRetrofit(
        @AuthorizedOkHttpClient client: OkHttpClient,
        json: Json
    ): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(json.asConverterFactory(APPLICATION_JSON.toMediaType()))
            .build()

    @UnauthorizedRetrofit
    @Provides
    @Singleton
    fun provideUnauthorizedRetrofit(
        @UnauthorizedOkHttpClient client: OkHttpClient,
        json: Json
    ): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(json.asConverterFactory(APPLICATION_JSON.toMediaType()))
            .build()

    @Provides
    fun provideAuthService(@UnauthorizedRetrofit retrofit: Retrofit): AuthService {
        return retrofit.create(AuthService::class.java)
    }

    @Provides
    fun provideLogoutService(@AuthorizedRetrofit retrofit: Retrofit): LogoutService {
        return retrofit.create(LogoutService::class.java)
    }

    @Provides
    fun provideRefreshService(@AuthorizedRetrofit retrofit: Retrofit): RefreshService {
        return retrofit.create(RefreshService::class.java)
    }

    @Provides
    fun provideRoomService(@AuthorizedRetrofit retrofit: Retrofit): RoomService {
        return retrofit.create(RoomService::class.java)
    }
}
