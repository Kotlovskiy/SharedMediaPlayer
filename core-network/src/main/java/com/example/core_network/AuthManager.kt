package com.example.core_network

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import net.openid.appauth.AuthState
import androidx.core.content.edit
import androidx.core.net.toUri
import com.example.storage.CryptoManager
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.ResponseTypeValues
import kotlin.concurrent.atomics.AtomicReference
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

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

    init {
        Log.d("AuthManager", "AuthManager initialized")
        Log.d("AuthManager", "Initial authState: ${authState.load()}")
    }

    private suspend fun getAuthState(): AuthState = suspendCancellableCoroutine { continuation ->
        Log.d("AuthManager", "getAuthState: Starting")
        val currentState = authState.load()
        Log.d("AuthManager", "getAuthState: Current state: $currentState")
        if (currentState.authorizationServiceConfiguration != null) {
            Log.d("AuthManager", "getAuthState: Configuration already exists, resuming")
            continuation.resume(currentState)
            return@suspendCancellableCoroutine
        }

        Log.d("AuthManager", "getAuthState: Fetching configuration from issuer: $ISSUER_URI")
        fetchConfiguration(
            issuerUri = ISSUER_URI.toUri(),
            onSuccess = { config ->
                Log.d("AuthManager", "getAuthState: Configuration fetched successfully")
                Log.d("AuthManager", "getAuthState: Config: $config")
                val newState = AuthState(config)
                authState.store(newState)
                saveAuthState(newState)
                Log.d("AuthManager", "getAuthState: New authState created and saved")
                continuation.resume(newState)
            },
            onError = { exception ->
                Log.e("AuthManager", "getAuthState: Failed to fetch configuration", exception)
                continuation.resumeWithException(exception)
            }
        )
    }

    fun getAccessToken() = runBlocking {
        Log.d("AuthManager", "getAccessToken: Called")
        val token = async { getAuthState().accessToken }.await()
        Log.d("AuthManager", "getAccessToken: Token: ${if (token != null) "[PRESENT]" else "[NULL]"}")
        token
    }

    private fun saveAuthState(authState: AuthState) {
        Log.d("AuthManager", "saveAuthState: Called")
        try {
            val jsonString = authState.jsonSerializeString()
            Log.d("AuthManager", "saveAuthState: JSON: $jsonString")
            val encrypted = cryptoManager.encryptBase64(jsonString)
            Log.d("AuthManager", "saveAuthState: Encrypted: $encrypted")
            preferences.edit {
                putString(AUTH_STATE, encrypted)
            }
            Log.d("AuthManager", "saveAuthState: Saved successfully")
        } catch (e: Exception) {
            Log.e("AuthManager", "saveAuthState: Failed to save", e)
        }
    }

    private fun restoreAuthState(): AuthState {
        Log.d("AuthManager", "restoreAuthState: Called")
        return try {
            val encrypted = preferences.getString(AUTH_STATE, null)
            Log.d("AuthManager", "restoreAuthState: Encrypted from prefs: ${if (encrypted != null) "[PRESENT]" else "[NULL]"}")

            val json = encrypted?.let {
                val decrypted = cryptoManager.decryptBase64(it)
                Log.d("AuthManager", "restoreAuthState: Decrypted: $decrypted")
                decrypted
            }

            if (json != null) {
                val restored = AuthState.jsonDeserialize(json)
                Log.d("AuthManager", "restoreAuthState: Restored authState successfully: $restored")
                restored
            } else {
                Log.d("AuthManager", "restoreAuthState: No JSON found, creating new AuthState")
                AuthState().also {
                    Log.d("AuthManager", "restoreAuthState: New AuthState created: $it")
                    saveAuthState(it)
                }
            }
        } catch (e: Exception) {
            Log.e("AuthManager", "restoreAuthState: Failed to restore, creating new AuthState", e)
            AuthState().also {
                Log.d("AuthManager", "restoreAuthState: New AuthState created after error: $it")
                saveAuthState(it)
            }
        }
    }

    @Synchronized
    private fun getAuthService(): AuthorizationService {
        Log.d("AuthManager", "getAuthService: Called")
        return authService ?: AuthorizationService(context).also {
            authService = it
            Log.d("AuthManager", "getAuthService: New AuthorizationService created")
        }
    }

    fun dispose() {
        Log.d("AuthManager", "dispose: Called")
        authService?.dispose()
        authService = null
        Log.d("AuthManager", "dispose: AuthService disposed")
    }

    fun authorizationIntentHandler(
        intent: Intent,
        onSuccess: () -> Unit,
        onAuthError: (AuthorizationException?) -> Unit
    ) {
        Log.d("AuthManager", "authorizationIntentHandler: Called")
        Log.d("AuthManager", "authorizationIntentHandler: Intent: $intent")

        val response = AuthorizationResponse.fromIntent(intent)
        val ex = AuthorizationException.fromIntent(intent)
        Log.i("AuthManager", "authorizationIntentHandler: Response: ${response?.toString() ?: "empty response"}")
        Log.i("AuthManager", "authorizationIntentHandler: Exception: ${ex?.message ?: "empty error"}")
        Log.i("AuthManager", "authorizationIntentHandler: Response JSON: ${response?.jsonSerializeString() ?: "N/A"}")
        Log.i("AuthManager", "authorizationIntentHandler: Exception JSON: ${ex?.toJsonString() ?: "N/A"}")

        if (response != null) {
            Log.d("AuthManager", "authorizationIntentHandler: Valid response, exchanging code for tokens")
            runBlocking { getAuthState().update(response, null) }
            exchangeCodeForTokens(
                authResponse = response,
                onAuthSuccess = {
                    Log.d("AuthManager", "authorizationIntentHandler: Token exchange successful")
                    onSuccess()
                },
                onAuthError = { error ->
                    Log.e("AuthManager", "authorizationIntentHandler: Token exchange failed", error)
                    onAuthError(error)
                }
            )
        } else if (ex != null) {
            Log.e("AuthManager", "authorizationIntentHandler: Received error from intent", ex)
            onAuthError(ex)
        } else {
            Log.w("AuthManager", "authorizationIntentHandler: Both response and exception are null")
        }
    }

    private fun fetchConfiguration(
        issuerUri: Uri,
        onSuccess: (AuthorizationServiceConfiguration) -> Unit,
        onError: (AuthorizationException) -> Unit
    ) {
        Log.d("AuthManager", "fetchConfiguration: Called with issuerUri: $issuerUri")
        AuthorizationServiceConfiguration.fetchFromIssuer(
            issuerUri,
            object : AuthorizationServiceConfiguration.RetrieveConfigurationCallback {
                override fun onFetchConfigurationCompleted(
                    serviceConfiguration: AuthorizationServiceConfiguration?,
                    ex: AuthorizationException?
                ) {
                    Log.d("AuthManager", "fetchConfiguration: Callback triggered")
                    if (ex != null) {
                        Log.e("AuthManager", "fetchConfiguration: Error occurred", ex)
                        Log.i("AuthManager", "fetchConfiguration: Error message: ${ex.message}")
                        Log.i("AuthManager", "fetchConfiguration: Error JSON: ${ex.toJsonString()}")
                        onError(ex)
                        return
                    }
                    serviceConfiguration?.let {
                        Log.d("AuthManager", "fetchConfiguration: Configuration retrieved successfully")
                        Log.i("AuthManager", "fetchConfiguration: Config JSON: ${it.toJsonString()}")
                        Log.i("AuthManager", "fetchConfiguration: Auth endpoint: ${it.authorizationEndpoint}")
                        Log.i("AuthManager", "fetchConfiguration: Token endpoint: ${it.tokenEndpoint}")
                        Log.i("AuthManager", "fetchConfiguration: End session endpoint: ${it.endSessionEndpoint}")
                        onSuccess(it)
                    } ?: run {
                        Log.w("AuthManager", "fetchConfiguration: Service configuration is null without error")
                    }
                }
            }
        )
    }

    suspend fun startAuthorization(
        launcher: ActivityResultLauncher<Intent>,
    ) {
        Log.d("AuthManager", "startAuthorization: Called")

        val config = getAuthState().authorizationServiceConfiguration
        Log.d("AuthManager", "startAuthorization: Config: $config")
        if (config == null) {
            Log.e("AuthManager", "startAuthorization: Configuration not fetched")
            throw IllegalStateException(CONFIG_NOT_FETCHED_EX)
        }

        Log.d("AuthManager", "startAuthorization: Building auth request")
        Log.d("AuthManager", "startAuthorization: CLIENT_ID: $CLIENT_ID")
        Log.d("AuthManager", "startAuthorization: REDIRECT_URI: $REDIRECT_URI")
        Log.d("AuthManager", "startAuthorization: BASE_SCOPE: $BASE_SCOPE")

        val authRequest = AuthorizationRequest.Builder(
            config,
            CLIENT_ID,
            ResponseTypeValues.CODE,
            REDIRECT_URI.toUri()
        )
            .setScope(BASE_SCOPE)
            .build()

        Log.d("AuthManager", "startAuthorization: Auth request built: $authRequest")
        Log.i("AuthManager", "startAuthorization: Auth request URI: ${authRequest.toUri()}")

        val authIntent = getAuthService().getAuthorizationRequestIntent(authRequest)
        Log.d("AuthManager", "startAuthorization: Auth intent created: $authIntent")
        launcher.launch(authIntent)
        Log.d("AuthManager", "startAuthorization: Launcher launched")
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
                Log.i("token", tokenResponse.jsonSerializeString())
                GlobalScope.launch { getAuthState().update(tokenResponse, null) }
                onAuthSuccess()
            } else {
                Log.i("token error", ex?.toJsonString() ?: "")
                onAuthError(ex)
            }
        }
    }

    fun refreshAccessToken(
        onSuccess: (String) -> Unit,
        onError: (AuthorizationException?) -> Unit
    ) {
        val currentAuthState = runBlocking { async { getAuthState() }.await() }
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

    suspend fun refreshAccessToken(): String? {
        Log.d("AuthManager", "refreshAccessToken: Called (suspend)")

        val currentAuthState = getAuthState()
        val refreshToken = currentAuthState.refreshToken
        if (refreshToken == null) {
            Log.e("AuthManager", "refreshAccessToken: No refresh token")
            return null
        }

        return suspendCancellableCoroutine { continuation ->
            val service = getAuthService()
            val tokenRequest = currentAuthState.createTokenRefreshRequest()

            service.performTokenRequest(tokenRequest) { tokenResponse, ex ->
                if (tokenResponse != null) {
                    Log.i("AuthManager", "refreshAccessToken: Token refresh successful")
                    currentAuthState.update(tokenResponse, null)
                    saveAuthState(currentAuthState)
                    val newAccessToken = currentAuthState.accessToken
                    if (newAccessToken != null) {
                        continuation.resume(newAccessToken)
                    } else {
                        continuation.resume(null)
                    }
                } else {
                    Log.e("AuthManager", "refreshAccessToken: Failed", ex)
                    continuation.resume(null)
                }
            }

            continuation.invokeOnCancellation {}
        }
    }

    companion object {
        private const val PREFERENCES_NAME = "auth"
        private const val AUTH_STATE = "auth_state"
        private const val ISSUER_URI =
            "https://97ad-104-128-139-225.ngrok-free.app/realms/music-realm"

        private const val BASE_SCOPE = "openid profile offline_access"

        private const val CLIENT_ID = "music-app"
        private const val REDIRECT_URI = "ru.music.app://callback"
        private const val CONFIG_NOT_FETCHED_EX = "Configuration not fetched"
        private const val NULL_ACCESS_TOKEN_EX = "Access token is null after refresh"
        private const val NO_REFRESH_TOKEN_AVAILABLE_EX = "No refresh token available"
    }
}
