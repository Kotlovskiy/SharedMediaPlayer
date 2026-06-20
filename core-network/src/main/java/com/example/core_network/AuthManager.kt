package com.example.core_network

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import net.openid.appauth.AuthState
import androidx.core.content.edit
import androidx.core.net.toUri
import com.example.storage.CryptoManager
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.ResponseTypeValues
import kotlin.concurrent.atomics.AtomicReference
import kotlin.concurrent.atomics.ExperimentalAtomicApi

@OptIn(ExperimentalAtomicApi::class)
class AuthManager (
    private val context: Context,
    private val cryptoManager: CryptoManager
) {
    private val preferences = context.getSharedPreferences(
        PREFERENCES_NAME,
        Context.MODE_PRIVATE
    )

    private val authState = AtomicReference(restoreAuthState())
    private var authService: AuthorizationService? = null

    private fun getAuthState(): AuthState {
        if (authState.load().authorizationServiceConfiguration == null) {
            fetchConfiguration(
                issuerUri = ISSUER_URI.toUri(),
                onSuccess = {
                    authState.store(
                        AuthState(it).also { newState ->
                            saveAuthState(newState)
                        }
                    )
                },
                onError = { throw (it) }
            )
        }
        return authState.load()
    }

    fun getAccessToken() = getAuthState().accessToken

    private fun saveAuthState(authState: AuthState) {
        preferences.edit {
            putString(
                AUTH_STATE,
                cryptoManager.encryptBase64(
                    authState.jsonSerializeString()
                )
            )
        }
    }

    private fun restoreAuthState(): AuthState {
        return try {
            val json = preferences.getString(AUTH_STATE, null)
                ?.let { cryptoManager.decryptBase64(it) }
            if (json != null) {
                AuthState.jsonDeserialize(json)
            } else {
                AuthState().also { saveAuthState(it) }
            }
        } catch (_: Exception) {
            AuthState().also { saveAuthState(it) }
        }
    }

    @Synchronized
    private fun getAuthService(): AuthorizationService {
        return authService ?: AuthorizationService(context).also {
            authService = it
        }
    }

    fun dispose() {
        authService?.dispose()
        authService = null
    }

    fun authorizationIntentHandler(
        intent: Intent,
        onSuccess: () -> Unit,
        onAuthError: (AuthorizationException?) -> Unit
    ) {
        val response = AuthorizationResponse.fromIntent(intent)
        val ex = AuthorizationException.fromIntent(intent)

        if (response != null) {
            exchangeCodeForTokens(
                authResponse = response,
                onAuthSuccess = onSuccess,
                onAuthError = { onAuthError(it) }
            )
        } else if (ex != null) {
            onAuthError(ex)
        }
    }

    private fun fetchConfiguration(
        issuerUri: Uri,
        onSuccess: (AuthorizationServiceConfiguration) -> Unit,
        onError: (AuthorizationException) -> Unit
    ) {
        AuthorizationServiceConfiguration.fetchFromIssuer(
            issuerUri,
            object : AuthorizationServiceConfiguration.RetrieveConfigurationCallback {
                override fun onFetchConfigurationCompleted(
                    serviceConfiguration: AuthorizationServiceConfiguration?,
                    ex: AuthorizationException?
                ) {
                    if (ex != null) {
                        onError(ex)
                        return
                    }
                    serviceConfiguration?.let {
                        onSuccess(it)
                    }
                }
            }
        )
    }

    fun startAuthorization(
        launcher: ActivityResultLauncher<Intent>,
    ) {
        val config = getAuthState().authorizationServiceConfiguration
            ?: throw IllegalStateException(CONFIG_NOT_FETCHED_EX)

        val authRequest = AuthorizationRequest.Builder(
            config,
            CLIENT_ID,
            ResponseTypeValues.CODE,
            REDIRECT_URI.toUri()
        )
            .setScope(BASE_SCOPE)
            .build()

        val authIntent = getAuthService().getAuthorizationRequestIntent(authRequest)
        launcher.launch(authIntent)
    }

    private fun exchangeCodeForTokens(
        authResponse: AuthorizationResponse,
        onAuthSuccess: () -> Unit,
        onAuthError: (AuthorizationException?) -> Unit
    ) {
        val service = getAuthService()
        val tokenRequest = authResponse.createTokenExchangeRequest()
        service.performTokenRequest(
            tokenRequest
        ) { tokenResponse, ex ->
            if (tokenResponse != null) {
                getAuthState().update(tokenResponse, null)
                onAuthSuccess()
            } else {
                onAuthError(ex)
            }
        }
    }

    fun refreshAccessToken(
        onSuccess: (String) -> Unit,
        onError: (AuthorizationException?) -> Unit
    ) {
        val currentAuthState = getAuthState()
        val refreshToken = currentAuthState.refreshToken

        if (refreshToken == null) {
            onError(AuthorizationException.fromTemplate(
                AuthorizationException.TokenRequestErrors.CLIENT_ERROR,
                IllegalStateException(NO_REFRESH_TOKEN_AVAILABLE_EX)
            ))
            return
        }

        val service = getAuthService()
        val tokenRequest = currentAuthState.createTokenRefreshRequest()

        service.performTokenRequest(
            tokenRequest
        ) { tokenResponse, ex ->
            if (tokenResponse != null) {
                currentAuthState.update(tokenResponse, null)
                saveAuthState(currentAuthState)
                val newAccessToken = currentAuthState.accessToken
                if (newAccessToken != null) {
                    onSuccess(newAccessToken)
                } else {
                    onError(AuthorizationException.fromTemplate(
                        AuthorizationException.TokenRequestErrors.INVALID_REQUEST,
                        IllegalStateException(NULL_ACCESS_TOKEN_EX)
                    ))
                }
            } else {
                onError(ex)
            }
        }
    }

    companion object {
        private const val PREFERENCES_NAME = "auth"
        private const val AUTH_STATE = "auth_state"
        private const val ISSUER_URI =
            "https://localhost:8081/realms/music-realm/.well-known/openid-configuration"

        private const val BASE_SCOPE = "openid profile offline_access"

        private const val CLIENT_ID = "music-app"
        private const val REDIRECT_URI = "com.music.app://callback"
        private const val CONFIG_NOT_FETCHED_EX = "Configuration not fetched"
        private const val NULL_ACCESS_TOKEN_EX = "Access token is null after refresh"
        private const val NO_REFRESH_TOKEN_AVAILABLE_EX = "No refresh token available"
    }
}
