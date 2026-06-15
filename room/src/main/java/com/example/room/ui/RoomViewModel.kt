package com.example.room.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.common_network_error.NetworkError
import com.example.room.data.RoomRepository
import com.example.room.domain.Participant
import com.example.room.domain.Result
import com.example.room.domain.Room
import com.example.room.domain.Song
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class RoomUiState {
    data class MusicTab(val list: List<Song>) : RoomUiState()
    data class ParticipantsTab(val list: List<Participant>) : RoomUiState()
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

sealed class RoomEffect {
    object ShowUnknownError: RoomEffect()
    object ShowForbiddenError: RoomEffect()
    object ShowNotFoundError: RoomEffect()
    object ShowInternetError: RoomEffect()
    object ShowServerError: RoomEffect()
    object UnauthorizedError: RoomEffect()
}

@HiltViewModel
class RoomViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val roomRepository: RoomRepository
) : ViewModel() {
    private val _state = MutableStateFlow<RoomUiState>(
        RoomUiState.MusicTab(listOf())
    )
    val state: StateFlow<RoomUiState> = _state

    private val _effect = Channel<RoomEffect>()
    val effect = _effect.receiveAsFlow()

    private val route = savedStateHandle.toRoute<RoomDestination>()
    val roomId = route.roomId
    val roomName = route.roomName

    init {
        viewModelScope.launch {
            when(val result = roomRepository.getRoom(roomId)) {
                is Result.Error -> sendErrorEffect(result.error)
                is Result.Success<Room> -> _state.emit(
                    RoomUiState.ParticipantsTab(result.data.participants)
                )
            }
        }
    }

    fun emit(intent: Intent) {
        viewModelScope.launch {
            when (intent) {
                is Intent.OnBack -> {  }
                is Intent.OnMusicsSwitch -> _state.emit(
                    RoomUiState.MusicTab(listOf())
                )
                is Intent.OnParticipantSettings -> {  }
                is Intent.OnParticipantsSwitch -> {
                    when(val result = roomRepository.getRoom(roomId)) {
                        is Result.Error -> sendErrorEffect(result.error)
                        is Result.Success<Room> -> _state.emit(
                            RoomUiState.ParticipantsTab(result.data.participants)
                        )
                    }
                }
                is Intent.OnAddParticipant -> {
                    val l = (_state.value as RoomUiState.ParticipantsTab).list
                    _state.emit(
                        RoomUiState.ParticipantsTab(
                            l + Participant(l.size.toString(), "P ${l.size}")
                        )
                    )
                }
                is Intent.OnAddSong -> {  }
                is Intent.OnDeleteSong -> {  }
            }
        }
    }

    private suspend fun sendErrorEffect(error: NetworkError) {
        when(error) {
            NetworkError.Conflict -> _effect.send(RoomEffect.ShowUnknownError)
            NetworkError.Forbidden -> _effect.send(RoomEffect.ShowForbiddenError)
            NetworkError.InvalidData -> _effect.send(RoomEffect.ShowUnknownError)
            NetworkError.NoInternet -> _effect.send(RoomEffect.ShowInternetError)
            NetworkError.NotFound -> _effect.send(RoomEffect.ShowNotFoundError)
            NetworkError.ServerError -> _effect.send(RoomEffect.ShowServerError)
            NetworkError.Unauthorized -> _effect.send(RoomEffect.UnauthorizedError)
            is NetworkError.Unknown -> _effect.send(RoomEffect.ShowUnknownError)
        }
    }
}
