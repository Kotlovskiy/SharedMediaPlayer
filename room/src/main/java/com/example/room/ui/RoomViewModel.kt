package com.example.room.ui

import android.content.ComponentName
import android.content.Context
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import androidx.navigation.toRoute
import com.example.common_network_error.NetworkError
import com.example.core_network.dto.queue.TrackResponse
import com.example.room.data.RoomRepository
import com.example.room.domain.Participant
import com.example.room.domain.Result
import com.example.room.domain.Room
import com.example.room.domain.Song
import com.example.room.service.PlaybackService
import com.google.common.util.concurrent.ListenableFuture
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.jvm.java

sealed class RoomUiState {
    data class MusicTab(val list: List<Song>) : RoomUiState()
    data class ParticipantsTab(
        val list: List<Participant>,
        val inviteCode: String
    ) : RoomUiState()
    class Error : RoomUiState()
}

sealed class Intent {
    class OnBack : Intent()
    class OnParticipantsSwitch : Intent()
    class OnMusicsSwitch : Intent()
    class OnParticipantSettings(id: String) : Intent()
    class OnAddParticipant : Intent()
    class OnAddSong : Intent()
    data class OnDeleteSong(val id: String) : Intent()
    object OnPlay : Intent()
    object OnPause : Intent()
    object OnNext : Intent()
}

sealed class RoomEffect {
    object ShowUnknownError: RoomEffect()
    object ShowForbiddenError: RoomEffect()
    object ShowNotFoundError: RoomEffect()
    object ShowInternetError: RoomEffect()
    object ShowServerError: RoomEffect()
    object UnauthorizedError: RoomEffect()
    data class AddSongDialogOpen(val roomId: String): RoomEffect()
    data class AddParticipantDialogOpen(val inviteCode: String): RoomEffect()
}

@HiltViewModel
class RoomViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val roomRepository: RoomRepository,
    @param:ApplicationContext private val appContext: Context,
) : ViewModel() {
    private val _state = MutableStateFlow<RoomUiState>(
        RoomUiState.MusicTab(listOf())
    )
    val state: StateFlow<RoomUiState> = _state

    private val _controller = MutableStateFlow<MediaController?>(null)
    val controller: StateFlow<MediaController?> = _controller

    private var controllerFuture: ListenableFuture<MediaController>? = null

    private val _effect = Channel<RoomEffect>()
    val effect = _effect.receiveAsFlow()

    private val route = savedStateHandle.toRoute<RoomDestination>()
    val roomId = route.roomId
    val roomName = route.roomName

    init {
        val sessionToken = SessionToken(
            appContext,
            ComponentName(appContext, PlaybackService::class.java)
        )

        controllerFuture = MediaController.Builder(appContext, sessionToken).buildAsync()

        controllerFuture?.addListener({
            val tempController = controllerFuture?.get()
            _controller.value = tempController
            tempController?.let { setupPlayer(it, roomId) }
        }, ContextCompat.getMainExecutor(appContext))

        viewModelScope.launch {
            when(val result = roomRepository.getRoom(roomId)) {
                is Result.Error -> sendErrorEffect(result.error)
                is Result.Success<Room> -> _state.emit(
                    RoomUiState.ParticipantsTab(
                        result.data.participants,
                        result.data.inviteCode
                    )
                )
            }
        }

        viewModelScope.launch {
            roomRepository.observeTrackChanges(roomId).collect {
                when(it) {
                    is Result.Error -> Log.i("RoomViewModel", "Error")
                    is Result.Success<TrackResponse> ->
                        Log.i("RoomViewModel", "trackId: ${it.data.id}")
                }
            }
        }
    }

    fun emit(intent: Intent) {
        viewModelScope.launch {
            when (intent) {
                is Intent.OnBack -> {  }
                is Intent.OnMusicsSwitch ->
                    when(val result = roomRepository.getQueue(roomId)) {
                        is Result.Error -> sendErrorEffect(result.error)
                        is Result.Success<List<Song>> -> _state.emit(
                            RoomUiState.MusicTab(
                                result.data,
                            )
                        )
                    }
                is Intent.OnParticipantSettings -> {  }
                is Intent.OnParticipantsSwitch -> {
                    when(val result = roomRepository.getRoom(roomId)) {
                        is Result.Error -> sendErrorEffect(result.error)
                        is Result.Success<Room> -> _state.emit(
                            RoomUiState.ParticipantsTab(
                                result.data.participants,
                                result.data.inviteCode
                            )
                        )
                    }
                }
                is Intent.OnAddParticipant -> {
                    val inviteCode = (_state.value as RoomUiState.ParticipantsTab).inviteCode
                    _effect.send(RoomEffect.AddParticipantDialogOpen(inviteCode = inviteCode))
                }
                is Intent.OnAddSong -> {
                    _effect.send(RoomEffect.AddSongDialogOpen(roomId))
                }
                is Intent.OnDeleteSong -> {
                    when(val result = roomRepository.deleteSong(roomId, intent.id)) {
                        is Result.Error -> sendErrorEffect(result.error)
                        is Result.Success<*> -> emit(Intent.OnMusicsSwitch())
                    }
                }
                Intent.OnPause -> { pause() }
                Intent.OnPlay -> { play() }
                Intent.OnNext -> {  }
            }
        }
    }

    private fun play() {
        _controller.value?.play()
    }

    private fun pause() {
        _controller.value?.pause()
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

    override fun onCleared() {
        super.onCleared()
        controllerFuture?.let {
            MediaController.releaseFuture(it)
        }
        _controller.value = null
    }

    @androidx.annotation.OptIn(UnstableApi::class)
    private fun setupPlayer(controller: MediaController, roomId: String) {
        val uri = "ws://0d30-104-128-139-225.ngrok-free.app/api/stream/$roomId"
        val mediaItem = MediaItem.fromUri(uri)
        controller.setMediaItem(mediaItem)
        controller.prepare()
        controller.playWhenReady = true
    }
}
