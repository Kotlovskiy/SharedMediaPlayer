package com.example.room.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.core_ui.icons.add_circle
import com.example.core_ui.icons.arrow_back
import com.example.core_ui.icons.delete
import com.example.core_ui.icons.more_vert
import com.example.core_ui.icons.settings
import com.example.core_ui.theme.Typography
import com.example.room.R

@Composable
fun RoomScreen(
    onBack: () -> Unit,
    onSettings: () -> Unit,
    viewModel: RoomViewModel = hiltViewModel()
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()
/*
    AppContainer(
        topBar = { RoomTopBar(
            onBack = onBack,
            onSettings = onSettings,
            title = when (uiState) {
                is RoomUiState.MusicTab -> (uiState as RoomUiState.MusicTab).title
                is RoomUiState.ParticipantsTab -> (uiState as RoomUiState.ParticipantsTab).title
                else -> ""
            }
        ) }
    ) {
        when (uiState) {
            is RoomUiState.Error -> {}
            is RoomUiState.MusicTab -> MusicTab(
                songs = (uiState as RoomUiState.MusicTab).list,
                onSwitchToParticipants = { viewModel.emit(Intent.OnParticipantsSwitch()) },
                onAddSong = { viewModel.emit(Intent.OnAddSong()) },
                onDeleteSong = { viewModel.emit(Intent.OnDeleteSong(it)) }
            )
            is RoomUiState.ParticipantsTab -> ParticipantsTab(
                participants = (uiState as RoomUiState.ParticipantsTab).list,
                onSwitchToMusic = { viewModel.emit(Intent.OnMusicsSwitch()) },
                onParticipantSettings = { viewModel.emit(Intent.OnParticipantSettings(it)) },
                onAddParticipant = { viewModel.emit(Intent.OnAddParticipant()) }
            )
        }
    }
 */
    when (uiState) {
        is RoomUiState.Error -> {}
        is RoomUiState.MusicTab -> MusicTab(
            songs = (uiState as RoomUiState.MusicTab).list,
            onSwitchToParticipants = { viewModel.emit(Intent.OnParticipantsSwitch()) },
            onAddSong = { viewModel.emit(Intent.OnAddSong()) },
            onDeleteSong = { viewModel.emit(Intent.OnDeleteSong(it)) }
        )
        is RoomUiState.ParticipantsTab -> ParticipantsTab(
            participants = (uiState as RoomUiState.ParticipantsTab).list,
            onSwitchToMusic = { viewModel.emit(Intent.OnMusicsSwitch()) },
            onParticipantSettings = { viewModel.emit(Intent.OnParticipantSettings(it)) },
            onAddParticipant = { viewModel.emit(Intent.OnAddParticipant()) }
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
    Column(modifier = modifier.fillMaxSize()) {
        RoomSegmentedTabs(
            selectedTab = Tab.MUSIC,
            onMusicSwitch = {},
            onParticipantsSwitch = onSwitchToParticipants
        )

        RoomAddButton(
            text = stringResource( R.string.add_song),
            contentDescription = "",
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
    Column(modifier = modifier.fillMaxSize()) {
        RoomSegmentedTabs(
            selectedTab = Tab.PARTICIPANTS,
            onMusicSwitch = onSwitchToMusic,
            onParticipantsSwitch = {}
        )

        RoomAddButton(
            text = stringResource(R.string.add_participant),
            contentDescription = "",
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
fun RoomTopBar(onBack: () -> Unit, onSettings: () -> Unit, title: String) {
    TopAppBar(
        windowInsets = WindowInsets(0, 0, 0, 0),
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
            IconButton(onClick = onSettings) {
                Icon(imageVector = settings, contentDescription = "")
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
                imageBitmap = item.image,
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
    imageBitmap: ImageBitmap,
    title: String,
    artist: String,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            bitmap = imageBitmap,
            contentDescription = null
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
