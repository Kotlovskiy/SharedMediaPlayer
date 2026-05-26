package com.example.sharedmediaplayer.settings

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

data class SettingsUiState(
    val title: String,
    val canAddSongs: Boolean,
    val canSwapSongs: Boolean,
    val canDeleteSongs: Boolean,
    val canSuspendSongs: Boolean,
    val canAddParticipants: Boolean,
    val canDeleteParticipants: Boolean
)

@HiltViewModel
class SettingsViewModel @Inject constructor(): ViewModel() {

}
