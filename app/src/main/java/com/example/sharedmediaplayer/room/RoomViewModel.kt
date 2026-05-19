package com.example.sharedmediaplayer.room

import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sharedmediaplayer.room.RoomUiState.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

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
    class MusicSuccess(val list: List<Song>) : RoomUiState()
    class ParticipantSuccess(val list: List<Participant>) : RoomUiState()
    class Error : RoomUiState()
}

sealed class Intent {
    class OnBack : Intent()
    class OnSettings : Intent()
    class OnParticipantsSwitch : Intent()
    class OnMusicsSwitch : Intent()
    class OnParticipantSettings(id: String) : Intent()
    class OnAddParticipant : Intent()
    class OnAddSong : Intent()
    class OnDeleteSong(id: String) : Intent()
}

class RoomViewModel() : ViewModel() {
    private val _state = MutableStateFlow<RoomUiState>(RoomUiState.MusicSuccess(listOf()))
    val state: StateFlow<RoomUiState> = _state

    fun emit(intent: Intent) {
        viewModelScope.launch {
            when(intent) {
                is Intent.OnBack -> TODO()
                is Intent.OnMusicsSwitch -> _state.emit(MusicSuccess(listOf()))
                is Intent.OnParticipantSettings -> TODO()
                is Intent.OnParticipantsSwitch -> _state.emit(ParticipantSuccess(listOf()))
                is Intent.OnSettings -> TODO()
                is Intent.OnAddParticipant -> {
                    val l = (_state.value as RoomUiState.ParticipantSuccess).list
                    _state.emit(
                        RoomUiState.ParticipantSuccess(
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
