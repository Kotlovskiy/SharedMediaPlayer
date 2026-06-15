package com.example.settings.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.core_ui.icons.arrow_back
import com.example.core_ui.theme.Typography
import com.example.settings.R

@Composable
fun Settings(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state = viewModel.state.collectAsStateWithLifecycle()
    Column(modifier = modifier) {
        val title = if (state.value.id == null) {
            stringResource(R.string.all_participants_can)
        } else {
            stringResource(R.string.participant_can, "№${state.value.id}")
        }

        Text(text = title, style = Typography.headlineMedium)
        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f)
        ) {
            val itemHeight = 60.dp
            item {
                SettingsListItem(
                    checked = state.value.canAddSongs,
                    onSwitch = { state -> viewModel.emit(Intent.SetCanAddSongs(state)) },
                    title = stringResource(R.string.add_songs),
                    modifier = Modifier.fillMaxWidth().height(itemHeight)
                )
            }
            item {
                SettingsListItem(
                    checked = state.value.canSwapSongs,
                    onSwitch = { state -> viewModel.emit(Intent.SetCanSwapSongs(state)) },
                    title = stringResource(R.string.swap_songs),
                    modifier = Modifier.fillMaxWidth().height(itemHeight)
                )
            }
            item {
                SettingsListItem(
                    checked = state.value.canDeleteSongs,
                    onSwitch = { state -> viewModel.emit(Intent.SetCanDeleteSongs(state)) },
                    title = stringResource(R.string.delete_songs),
                    modifier = Modifier.fillMaxWidth().height(itemHeight)
                )
            }
            item {
                SettingsListItem(
                    checked = state.value.canSuspendSongs,
                    onSwitch = { state -> viewModel.emit(Intent.SetCanSuspendSongs(state)) },
                    title = stringResource(R.string.suspend_song),
                    modifier = Modifier.fillMaxWidth().height(itemHeight)
                )
            }
            item {
                SettingsListItem(
                    checked = state.value.canAddParticipants,
                    onSwitch = { state -> viewModel.emit(Intent.SetCanAddParticipants(state)) },
                    title = stringResource(R.string.add_participants),
                    modifier = Modifier.fillMaxWidth().height(itemHeight)
                )
            }
            item {
                SettingsListItem(
                    checked = state.value.canDeleteParticipants,
                    onSwitch = { state -> viewModel.emit(Intent.SetCanDeleteParticipants(state)) },
                    title = stringResource(R.string.delete_participants),
                    modifier = Modifier.fillMaxWidth().height(itemHeight)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsTopBar(
    onBack: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.rights_settings),
                style = Typography.titleLarge
            )
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(imageVector = arrow_back, contentDescription = "")
            }
        }
    )
}

@Composable
fun SettingsListItem(
    checked: Boolean,
    onSwitch: (Boolean) -> Unit,
    title: String,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Text(
            modifier = Modifier.weight(1f),
            text = title,
            style = Typography.titleLarge
        )

        Switch(
            checked = checked,
            onCheckedChange = { newState ->
                onSwitch(newState)
            }
        )
    }
}
