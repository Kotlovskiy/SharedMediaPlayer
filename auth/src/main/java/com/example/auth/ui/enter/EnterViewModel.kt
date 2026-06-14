package com.example.auth.ui.enter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.auth.data.AuthDataSource
import com.example.core_network.dto.AuthRequest
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

sealed class NavAction {
    object ToRegistration : NavAction()
    object ToMainScreen : NavAction()
}

@HiltViewModel
class EnterViewModel @Inject constructor(
    private val dataSource: AuthDataSource
) : ViewModel() {
    private val _uiState = MutableStateFlow(EnterUiState(email = "", password = ""))
    val uiState: StateFlow<EnterUiState> = _uiState

    private val _actions = MutableSharedFlow<NavAction>(replay = 0)
    val actions = _actions.asSharedFlow()

    fun emit(intent: EnterIntent) {
        viewModelScope.launch {
            when (intent) {
                is EnterIntent.ClickEnter -> {
                    dataSource.login(AuthRequest(uiState.value.email, uiState.value.password))
                    _actions.emit(NavAction.ToMainScreen)
                }
                is EnterIntent.SetEmail -> _uiState.update { currentState ->
                    currentState.copy(email = intent.email)
                }
                is EnterIntent.SetPassword -> _uiState.update { currentState ->
                    currentState.copy(password = intent.password)
                }
                is EnterIntent.ClickToRegistration -> _actions.emit(NavAction.ToRegistration)
            }
        }
    }
}
