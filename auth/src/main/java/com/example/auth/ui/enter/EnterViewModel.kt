package com.example.auth.ui.enter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.auth.data.AuthRepository
import com.example.auth.domain.Result
import com.example.common_network_error.NetworkError
import com.example.core_network.AuthManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EnterUiState(val email: String, val password: String)

sealed class EnterIntent {
    class SetEmail(val email: String) : EnterIntent()
    class SetPassword(val password: String) : EnterIntent()
    object ClickEnter : EnterIntent()
    object ClickToRegistration : EnterIntent()
}

sealed class AuthEffect {
    object ToRegistration : AuthEffect()
    object ToMainScreen : AuthEffect()
    object ShowUnknownError: AuthEffect()
    object ShowForbiddenError: AuthEffect()
    object ShowInvalidDataError: AuthEffect()
    object ShowInternetError: AuthEffect()
    object ShowServerError: AuthEffect()
}

@HiltViewModel
class EnterViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    val authManager: AuthManager
) : ViewModel() {
    private val _uiState = MutableStateFlow(EnterUiState(email = "", password = ""))
    val uiState: StateFlow<EnterUiState> = _uiState

    private val _actions = MutableSharedFlow<AuthEffect>(replay = 0)
    val actions = _actions.asSharedFlow()

    fun emit(intent: EnterIntent) {
        viewModelScope.launch {
            when (intent) {
                is EnterIntent.ClickEnter -> {
                    val result = authRepository.login(uiState.value.email, uiState.value.password)
                    when(result) {
                        is Result.Error -> sendErrorEffect(result.error)
                        is Result.Success<*> -> _actions.emit(AuthEffect.ToMainScreen)
                    }
                }
                is EnterIntent.SetEmail -> _uiState.update { currentState ->
                    currentState.copy(email = intent.email)
                }
                is EnterIntent.SetPassword -> _uiState.update { currentState ->
                    currentState.copy(password = intent.password)
                }
                is EnterIntent.ClickToRegistration -> _actions.emit(AuthEffect.ToRegistration)
            }
        }
    }

    private suspend fun sendErrorEffect(error: NetworkError) {
        when(error) {
            NetworkError.Conflict -> _actions.emit(AuthEffect.ShowUnknownError)
            NetworkError.Forbidden -> _actions.emit(AuthEffect.ShowForbiddenError)
            NetworkError.InvalidData -> _actions.emit(AuthEffect.ShowInvalidDataError)
            NetworkError.NoInternet -> _actions.emit(AuthEffect.ShowInternetError)
            NetworkError.NotFound -> _actions.emit(AuthEffect.ShowUnknownError)
            NetworkError.ServerError -> _actions.emit(AuthEffect.ShowServerError)
            NetworkError.Unauthorized -> _actions.emit(AuthEffect.ShowUnknownError)
            is NetworkError.Unknown -> _actions.emit(AuthEffect.ShowUnknownError)
        }
    }

    override fun onCleared() {
        super.onCleared()
        authManager.dispose()
    }
}
