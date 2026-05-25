package com.example.sharedmediaplayer.room

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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sharedmediaplayer.R
import com.example.sharedmediaplayer.ui.icons.add_circle
import com.example.sharedmediaplayer.ui.icons.arrow_back
import com.example.sharedmediaplayer.ui.icons.delete
import com.example.sharedmediaplayer.ui.icons.more_vert
import com.example.sharedmediaplayer.ui.icons.settings
import com.example.sharedmediaplayer.ui.theme.Typography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Room(
    viewModel: RoomViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        RoomSegmentedTabs(
            currentState = uiState,
            onMusicSwitch = { viewModel.emit(Intent.OnMusicsSwitch()) },
            onParticipantsSwitch = { viewModel.emit(Intent.OnParticipantsSwitch()) }
        )

        RoomAddButton(
            uiState = uiState,
            onAddSong = { viewModel.emit(Intent.OnAddSong()) },
            onAddParticipant = { viewModel.emit(Intent.OnAddParticipant()) }
        )

        RoomContent(
            uiState = uiState,
            onDeleteSong = { viewModel.emit(Intent.OnDeleteSong(it)) },
            onParticipantSettings = { viewModel.emit(Intent.OnParticipantSettings(it)) }
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
    currentState: RoomUiState,
    onMusicSwitch: () -> Unit,
    onParticipantsSwitch: () -> Unit
) {
    SingleChoiceSegmentedButtonRow(
        modifier = Modifier.fillMaxWidth(),
    ) {
        SegmentedButton(
            selected = currentState is RoomUiState.MusicSuccess,
            onClick = onMusicSwitch,
            shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
        ) {
            Text(text = stringResource(R.string.music), style = Typography.labelLarge)
        }
        SegmentedButton(
            selected = currentState is RoomUiState.ParticipantSuccess,
            onClick = onParticipantsSwitch,
            shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
        ) {
            Text(text = stringResource(R.string.participants), style = Typography.labelLarge)
        }
    }
}

@Composable
private fun RoomAddButton(
    uiState: RoomUiState,
    onAddSong: () -> Unit,
    onAddParticipant: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val (addText, onAddClick) = when (uiState) {
            is RoomUiState.MusicSuccess -> Pair(
                stringResource(R.string.add_song),
                onAddSong
            )
            is RoomUiState.ParticipantSuccess -> Pair(
                stringResource(R.string.add_participant),
                onAddParticipant
            )
            is RoomUiState.Error -> Pair("", {})
        }

        Text(text = addText, style = Typography.headlineSmall)
        IconButton(onClick = onAddClick) {
            Icon(imageVector = add_circle, contentDescription = "")
        }
    }
}


@Composable
private fun RoomContent(
    uiState: RoomUiState,
    onDeleteSong: (String) -> Unit,
    onParticipantSettings: (String) -> Unit
) {
    when (uiState) {
        is RoomUiState.MusicSuccess -> {
            MusicList(list = uiState.list, onDelete = onDeleteSong)
        }
        is RoomUiState.ParticipantSuccess -> {
            ParticipantList(list = uiState.list, onMore = onParticipantSettings)
        }
        is RoomUiState.Error -> {}
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
