package com.example.hello.ui.dialog.join

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common_network_error.NetworkError
import com.example.hello.data.InviteRepository
import com.example.hello.domain.Result
import com.example.hello.domain.Room
import com.example.hello.ui.dialog.Effect
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JoinRoomDialogViewModel @Inject constructor(
    private val inviteRepository: InviteRepository
): ViewModel() {
    private val _state = MutableStateFlow("")
    val state = _state.asStateFlow()

    private val _effect = Channel<Effect>()
    val effect = _effect.receiveAsFlow()

    fun setInviteCode(newName: String) {
        _state.update { newName }
    }

    fun clickConfirm() {
        viewModelScope.launch {
            when(val result = inviteRepository.joinRoom(_state.value)) {
                is Result.Error -> sendErrorEffect(result.error)
                is Result.Success<Room> -> _effect.send(
                    Effect.NavigateToRoom(
                        id = result.data.id,
                        name = result.data.name
                    )
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
