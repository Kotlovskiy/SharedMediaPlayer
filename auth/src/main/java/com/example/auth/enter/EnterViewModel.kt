package com.example.auth.enter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EnterUiState(val email: String, val password: String)

sealed class EnterIntent {
    class SetEmail(val email: String) : EnterIntent()
    class SetPassword(val password: String) : EnterIntent()
    object ClickEnter : EnterIntent()
}

@HiltViewModel
class EnterViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(EnterUiState(email = "", password = ""))
    val uiState: StateFlow<EnterUiState> = _uiState

    fun emit(intent: EnterIntent) {
        viewModelScope.launch {
            when(intent) {
                EnterIntent.ClickEnter -> TODO()
                is EnterIntent.SetEmail -> _uiState.update { currentState ->
                    currentState.copy(email = intent.email)
                }
                is EnterIntent.SetPassword -> _uiState.update { currentState ->
                    currentState.copy(password = intent.password)
                }
            }
        }
    }
}
