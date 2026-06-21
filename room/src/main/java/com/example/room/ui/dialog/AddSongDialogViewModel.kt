package com.example.room.ui.dialog

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.common_network_error.NetworkError
import com.example.room.domain.Result
import com.example.room.data.RoomRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class Effect {
    object ShowInternetError: Effect()
    object ShowInvalidDataError: Effect()
    object UnauthorizedError: Effect()
    object ShowForbiddenError: Effect()
    object ShowNotFoundError: Effect()
    object ShowConflictError: Effect()
    object ShowServerError: Effect()
    object ShowUnknownError: Effect()
    object SongWasAdded: Effect()
}

@HiltViewModel
class AddSongDialogViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: RoomRepository
) : ViewModel() {
    private val roomId = savedStateHandle.toRoute<AddSongDialogDestination>().roomId
    private val _state = MutableStateFlow("")
    val state = _state.asStateFlow()

    private val _effect = Channel<Effect>()
    val effect = _effect.receiveAsFlow()

    fun setUrl(newName: String) {
        _state.update { newName }
    }

    fun clickConfirm() {
        viewModelScope.launch {
            when(val result = repository.addSong(
                roomId = roomId, url = _state.value
            )) {
                is Result.Error -> sendErrorEffect(result.error)
                is Result.Success<*> -> _effect.send(
                    Effect.SongWasAdded
                )
            }
        }
    }

    private suspend fun sendErrorEffect(error: NetworkError) {
        when(error) {
            NetworkError.Conflict -> _effect.send(Effect.ShowUnknownError)
            NetworkError.Forbidden -> _effect.send(Effect.ShowForbiddenError)
            NetworkError.InvalidData -> _effect.send(Effect.ShowInvalidDataError)
            NetworkError.NoInternet -> _effect.send(Effect.ShowInternetError)
            NetworkError.NotFound -> _effect.send(Effect.ShowNotFoundError)
            NetworkError.ServerError -> _effect.send(Effect.ShowServerError)
            NetworkError.Unauthorized -> _effect.send(Effect.UnauthorizedError)
            is NetworkError.Unknown -> _effect.send(Effect.ShowUnknownError)
        }
    }
}
