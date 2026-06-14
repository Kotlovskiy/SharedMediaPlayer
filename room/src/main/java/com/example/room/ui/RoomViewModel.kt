package com.example.room.ui

import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class Song(
    val id: String,
    val image: ImageBitmap,
    val title: String,
    val artist: String
)

data class Participant(
    val id: String,
    val name: String
)

sealed class RoomUiState {
    data class MusicTab(val title: String, val list: List<Song>) : RoomUiState()
    data class ParticipantsTab(val title: String, val list: List<Participant>) : RoomUiState()
    class Error : RoomUiState()
}

sealed class Intent {
    class OnBack : Intent()
    class OnParticipantsSwitch : Intent()
    class OnMusicsSwitch : Intent()
    class OnParticipantSettings(id: String) : Intent()
    class OnAddParticipant : Intent()
    class OnAddSong : Intent()
    class OnDeleteSong(id: String) : Intent()
}

@HiltViewModel
class RoomViewModel @Inject constructor() : ViewModel() {
    private val _state = MutableStateFlow<RoomUiState>(
        RoomUiState.MusicTab(
            "1111",
            listOf()
        )
    )
    val state: StateFlow<RoomUiState> = _state

    fun emit(intent: Intent) {
        viewModelScope.launch {
            when (intent) {
                is Intent.OnBack -> TODO()
                is Intent.OnMusicsSwitch -> _state.emit(
                    RoomUiState.MusicTab(
                        "1111",
                        listOf()
                    )
                )
                is Intent.OnParticipantSettings -> TODO()
                is Intent.OnParticipantsSwitch -> _state.emit(
                    RoomUiState.ParticipantsTab(
                        "1111",
                        listOf()
                    )
                )
                is Intent.OnAddParticipant -> {
                    val l = (_state.value as RoomUiState.ParticipantsTab).list
                    _state.emit(
                        RoomUiState.ParticipantsTab(
                            "1111",
                            l + Participant(l.size.toString(), "P ${l.size}")
                        )
                    )
                }
                is Intent.OnAddSong -> TODO()
                is Intent.OnDeleteSong -> TODO()
            }
        }
    }
}
