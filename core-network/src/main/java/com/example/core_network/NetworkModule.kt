package com.example.core_network

import android.content.Context
import com.example.core_network.NetworkConstants.APPLICATION_JSON
import com.example.core_network.NetworkConstants.BASE_URL
import com.example.core_network.interceptor.TokenAuthenticator
import com.example.core_network.interceptor.TokenInterceptor
import com.example.core_network.qualifier.AuthorizedOkHttpClient
import com.example.core_network.qualifier.AuthorizedRetrofit
import com.example.core_network.qualifier.UnauthorizedOkHttpClient
import com.example.core_network.qualifier.UnauthorizedRetrofit
import com.example.core_network.service.AuthService
import com.example.core_network.service.LogoutService
import com.example.core_network.service.QueueService
import com.example.core_network.service.RefreshService
import com.example.core_network.service.RoomService
import com.example.storage.CryptoManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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
    @Singleton
    fun provideAuthManager(
        @ApplicationContext context: Context,
        cryptoManager: CryptoManager
    ): AuthManager {
        return AuthManager(context, cryptoManager)
    }

    @Provides
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC)
    }

    @UnauthorizedOkHttpClient
    @Provides
    @Singleton
    fun provideUnauthorizedOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
    ): OkHttpClient =
        OkHttpClient.Builder()
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

    @Provides
    fun provideQueueService(@AuthorizedRetrofit retrofit: Retrofit): QueueService {
        return retrofit.create(QueueService::class.java)
    }
}
