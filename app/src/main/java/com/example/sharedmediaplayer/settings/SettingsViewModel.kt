package com.example.sharedmediaplayer.settings

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val id: Int?,
    val canAddSongs: Boolean,
    val canSwapSongs: Boolean,
    val canDeleteSongs: Boolean,
    val canSuspendSongs: Boolean,
    val canAddParticipants: Boolean,
    val canDeleteParticipants: Boolean
)

sealed class Intent {
    class SetCanAddSongs(val newState: Boolean) : Intent()
    class SetCanSwapSongs(val newState: Boolean) : Intent()
    class SetCanDeleteSongs(val newState: Boolean) : Intent()
    class SetCanSuspendSongs(val newState: Boolean) : Intent()
    class SetCanAddParticipants(val newState: Boolean) : Intent()
    class SetCanDeleteParticipants(val newState: Boolean) : Intent()
}

@HiltViewModel
class SettingsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
): ViewModel() {
    private val route = savedStateHandle.toRoute<SettingsDestination>()
    private val _state = MutableStateFlow(SettingsUiState(
        id = route.id,
        canAddSongs = false,
        canSwapSongs = false,
        canDeleteSongs = false,
        canSuspendSongs = false,
        canAddParticipants = false,
        canDeleteParticipants = false
    ))
    val state: StateFlow<SettingsUiState> = _state

    fun emit(intent: Intent) {
        viewModelScope.launch {
            when (intent) {
                is Intent.SetCanAddParticipants -> {
                    _state.update { currentState ->
                        currentState.copy(canAddParticipants = intent.newState)
                    }
                }
                is Intent.SetCanAddSongs -> {
                    _state.update { currentState ->
                        currentState.copy(canAddSongs = intent.newState)
                    }
                }
                is Intent.SetCanDeleteParticipants -> {
                    _state.update { currentState ->
                        currentState.copy(canDeleteParticipants = intent.newState)
                    }
                }
                is Intent.SetCanDeleteSongs -> {
                    _state.update { currentState ->
                        currentState.copy(canDeleteSongs = intent.newState)
                    }
                }
                is Intent.SetCanSuspendSongs -> {
                    _state.update { currentState ->
                        currentState.copy(canSuspendSongs = intent.newState)
                    }
                }
                is Intent.SetCanSwapSongs -> {
                    _state.update { currentState ->
                        currentState.copy(canSwapSongs = intent.newState)
                    }
                }
            }
        }
    }
}
