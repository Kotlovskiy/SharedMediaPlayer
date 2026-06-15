package com.example.room.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import coil3.compose.AsyncImage
import com.example.core_ui.icons.add_circle
import com.example.core_ui.icons.arrow_back
import com.example.core_ui.icons.delete
import com.example.core_ui.icons.image
import com.example.core_ui.icons.more_vert
import com.example.core_ui.icons.settings
import com.example.core_ui.theme.Typography
import com.example.room.R
import com.example.room.domain.Participant
import com.example.room.domain.Song

@Composable
fun RoomScreen(
    setTopBarParams: (RoomTopBarParams) -> Unit,
    showError: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RoomViewModel = hiltViewModel()
) {
    val commonErrorText = stringResource(com.example.core_ui.R.string.smt_went_wrong)
    val internetErrorText = stringResource(com.example.core_ui.R.string.check_connection)
    val serverErrorText = stringResource(com.example.core_ui.R.string.server_error)
    val notFoundErrorText = stringResource(R.string.not_found)
    val forbiddenErrorText = stringResource(R.string.access_denied)

    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current

    setTopBarParams(
        RoomTopBarParams(
            roomId = viewModel.roomId,
            roomName = viewModel.roomName,
            showSettingsButton = true,
            onExit = { viewModel.emit(Intent.OnBack()) }
        )
    )

    LaunchedEffect(Unit) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.effect.collect { effect ->
                when(effect) {
                    RoomEffect.ShowUnknownError -> showError(commonErrorText)
                    RoomEffect.ShowInternetError -> showError(internetErrorText)
                    RoomEffect.ShowServerError -> showError(serverErrorText)
                    RoomEffect.UnauthorizedError -> {/*onLogoutAction()*/}
                    RoomEffect.ShowForbiddenError -> showError(forbiddenErrorText)
                    RoomEffect.ShowNotFoundError -> showError(notFoundErrorText)
                }
            }
        }
    }

    when (uiState) {
        is RoomUiState.Error -> {}
        is RoomUiState.MusicTab -> MusicTab(
            songs = (uiState as RoomUiState.MusicTab).list,
            onSwitchToParticipants = { viewModel.emit(Intent.OnParticipantsSwitch()) },
            onAddSong = { viewModel.emit(Intent.OnAddSong()) },
            onDeleteSong = { viewModel.emit(Intent.OnDeleteSong(it)) },
            modifier = modifier
        )
        is RoomUiState.ParticipantsTab -> ParticipantsTab(
            participants = (uiState as RoomUiState.ParticipantsTab).list,
            onSwitchToMusic = { viewModel.emit(Intent.OnMusicsSwitch()) },
            onParticipantSettings = { viewModel.emit(Intent.OnParticipantSettings(it)) },
            onAddParticipant = { viewModel.emit(Intent.OnAddParticipant()) },
            modifier = modifier
        )
    }
}

@Composable
private fun MusicTab(
    songs: List<Song>,
    onSwitchToParticipants: () -> Unit,
    onAddSong: () -> Unit,
    onDeleteSong: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        RoomSegmentedTabs(
            selectedTab = Tab.MUSIC,
            onMusicSwitch = {},
            onParticipantsSwitch = onSwitchToParticipants
        )

        RoomAddButton(
            text = stringResource( R.string.add_song),
            contentDescription = stringResource(R.string.add_song_description),
            onClick = onAddSong
        )

        MusicList(list = songs, onDelete = onDeleteSong)
    }
}

@Composable
private fun ParticipantsTab(
    participants: List<Participant>,
    onSwitchToMusic: () -> Unit,
    onAddParticipant: () -> Unit,
    onParticipantSettings: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        RoomSegmentedTabs(
            selectedTab = Tab.PARTICIPANTS,
            onMusicSwitch = onSwitchToMusic,
            onParticipantsSwitch = {}
        )

        RoomAddButton(
            text = stringResource(R.string.add_participant),
            contentDescription = stringResource(R.string.add_participant_description),
            onClick = onAddParticipant
        )

        ParticipantList(
            list = participants,
            onMore = onParticipantSettings
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomTopBar(
    onBack: () -> Unit,
    onSettings: () -> Unit,
    title: String,
    showSettingsButton: Boolean
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                style = Typography.titleLarge
            )
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(imageVector = arrow_back, contentDescription = "")
            }
        },
        actions = {
            if(showSettingsButton) {
                IconButton(onClick = onSettings) {
                    Icon(imageVector = settings, contentDescription = "")
                }
            }
        }
    )
}

@Composable
private fun RoomSegmentedTabs(
    selectedTab: Tab,
    onMusicSwitch: () -> Unit,
    onParticipantsSwitch: () -> Unit
) {
    SingleChoiceSegmentedButtonRow(
        modifier = Modifier.fillMaxWidth(),
    ) {
        SegmentedButton(
            selected = selectedTab == Tab.MUSIC,
            onClick = onMusicSwitch,
            shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
        ) {
            Text(text = stringResource(R.string.music), style = Typography.labelLarge)
        }
        SegmentedButton(
            selected = selectedTab == Tab.PARTICIPANTS,
            onClick = onParticipantsSwitch,
            shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
        ) {
            Text(text = stringResource(R.string.participants), style = Typography.labelLarge)
        }
    }
}

@Composable
private fun RoomAddButton(
    text: String,
    contentDescription: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = text, style = Typography.headlineSmall)
        IconButton(onClick = onClick) {
            Icon(imageVector = add_circle, contentDescription = contentDescription)
        }
    }
}

@Composable
private fun MusicList(
    list: List<Song>,
    onDelete: (String) -> Unit
) {
    LazyColumn {
        itemsIndexed(
            items = list,
            key = { _, item -> item.id }
        ) { _, item ->
            SongItem(
                url = item.url,
                title = item.title,
                artist = item.artist,
                onDelete = { onDelete(item.id) }
            )
        }
    }
}

@Composable
private fun ParticipantList(
    list: List<Participant>,
    onMore: (String) -> Unit
) {
    LazyColumn {
        itemsIndexed(
            items = list,
            key = { _, item -> item.id }
        ) { _, item ->
            ParticipantItem(
                participant = item.name,
                onMore = { onMore(item.id) }
            )
        }
    }
}

@Composable
private fun SongItem(
    url: String,
    title: String,
    artist: String,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = url,
            contentDescription = null,
            placeholder = rememberVectorPainter(image)
        )
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(text = title, style = Typography.titleLarge)
            Text(text = artist, style = Typography.bodyMedium)
        }
        IconButton(onClick = { onDelete() }) {
            Icon(imageVector = delete, contentDescription = "")
        }
    }
}

@Composable
private fun ParticipantItem(
    participant: String,
    onMore: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = participant,
            style = Typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        IconButton(
            onClick = { onMore() }
        ) {
            Icon(
                imageVector = more_vert,
                contentDescription = ""
            )
        }
    }
}

private enum class Tab { MUSIC, PARTICIPANTS }
