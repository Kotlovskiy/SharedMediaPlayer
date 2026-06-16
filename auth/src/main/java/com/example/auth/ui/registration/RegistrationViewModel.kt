package com.example.auth.ui.registration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.auth.data.AuthRepository
import com.example.auth.domain.Result
import com.example.common_network_error.NetworkError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RegistrationUiState(val email: String, val nickname: String, val password: String)

sealed class RegistrationIntent {
    class SetEmail(val email: String) : RegistrationIntent()
    class SetPassword(val password: String) : RegistrationIntent()
    class SetNickname(val nickname: String) : RegistrationIntent()
    object ClickRegister : RegistrationIntent()
    object ClickToEnter : RegistrationIntent()
}

sealed class AuthEffect {
    object ToEnter : AuthEffect()
    object ToMainScreen : AuthEffect()
    object ShowUnknownError: AuthEffect()
    object ShowForbiddenError: AuthEffect()
    object ShowConflictError: AuthEffect()
    object ShowInvalidDataError: AuthEffect()
    object ShowInternetError: AuthEffect()
    object ShowServerError: AuthEffect()
}

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(RegistrationUiState(
        email = "",
        nickname = "",
        password = ""
    ))
    val uiState: StateFlow<RegistrationUiState> = _uiState

    private val _actions = MutableSharedFlow<AuthEffect>(replay = 0)
    val actions = _actions.asSharedFlow()

    fun emit(intent: RegistrationIntent) {
        viewModelScope.launch {
            when (intent) {
                is RegistrationIntent.ClickRegister -> {
                    val result = authRepository.register(
                        nickname = uiState.value.nickname,
                        email = uiState.value.email,
                        password = uiState.value.password
                    )
                    when(result) {
                        is Result.Error -> sendErrorEffect(result.error)
                        is Result.Success<*> -> _actions.emit(AuthEffect.ToMainScreen)
                    }
                }
                is RegistrationIntent.ClickToEnter -> _actions.emit(AuthEffect.ToEnter)
                is RegistrationIntent.SetEmail -> _uiState.update { currentState ->
                    currentState.copy(email = intent.email)
                }
                is RegistrationIntent.SetPassword -> _uiState.update { currentState ->
                    currentState.copy(password = intent.password)
                }
                is RegistrationIntent.SetNickname -> _uiState.update { currentState ->
                    currentState.copy(nickname = intent.nickname)
                }
            }
        }
    }

    private suspend fun sendErrorEffect(error: NetworkError) {
        when(error) {
            NetworkError.Conflict -> _actions.emit(AuthEffect.ShowConflictError)
            NetworkError.Forbidden -> _actions.emit(AuthEffect.ShowForbiddenError)
            NetworkError.InvalidData -> _actions.emit(AuthEffect.ShowInvalidDataError)
            NetworkError.NoInternet -> _actions.emit(AuthEffect.ShowInternetError)
            NetworkError.NotFound -> _actions.emit(AuthEffect.ShowUnknownError)
            NetworkError.ServerError -> _actions.emit(AuthEffect.ShowServerError)
            NetworkError.Unauthorized -> _actions.emit(AuthEffect.ShowUnknownError)
            is NetworkError.Unknown -> _actions.emit(AuthEffect.ShowUnknownError)
        }
    }
}
