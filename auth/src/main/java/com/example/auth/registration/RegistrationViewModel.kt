package com.example.auth.registration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

sealed class NavAction {
    object ToEnter : NavAction()
    object ToMainScreen : NavAction()
}

@HiltViewModel
class RegistrationViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(RegistrationUiState(
        email = "",
        nickname = "",
        password = ""
    ))
    val uiState: StateFlow<RegistrationUiState> = _uiState

    private val _actions = MutableSharedFlow<NavAction>(replay = 0)
    val actions = _actions.asSharedFlow()

    fun emit(intent: RegistrationIntent) {
        viewModelScope.launch {
            when(intent) {
                is RegistrationIntent.ClickRegister -> _actions.emit(NavAction.ToMainScreen)
                is RegistrationIntent.ClickToEnter -> _actions.emit(NavAction.ToEnter)
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
}
