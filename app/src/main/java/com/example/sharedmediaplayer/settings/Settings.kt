package com.example.sharedmediaplayer.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
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
import com.example.sharedmediaplayer.R
import com.example.sharedmediaplayer.ui.icons.arrow_back
import com.example.sharedmediaplayer.ui.theme.Typography

@Composable
fun Settings(
    id: Int?,
) {
    Column {
        val title = if(id == null) {
            stringResource(R.string.all_participants_can)
        } else {
            stringResource(R.string.participant_can, "№${id}")
        }
        Text(text = title, style = Typography.headlineMedium)
        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f)
        ) {
            val itemHeight = 60.dp
            item {
                SettingsListItem(
                    checked = true,
                    onSwitch = {},
                    title = stringResource(R.string.add_songs),
                    modifier = Modifier.fillMaxWidth().height(itemHeight)
                )
            }
            item {
                SettingsListItem(
                    checked = true,
                    onSwitch = {},
                    title = stringResource(R.string.swap_songs),
                    modifier = Modifier.fillMaxWidth().height(itemHeight)
                )
            }
            item {
                SettingsListItem(
                    checked = true,
                    onSwitch = {},
                    title = stringResource(R.string.delete_songs),
                    modifier = Modifier.fillMaxWidth().height(itemHeight)
                )
            }
            item {
                SettingsListItem(
                    checked = true,
                    onSwitch = {},
                    title = stringResource(R.string.suspend_song),
                    modifier = Modifier.fillMaxWidth().height(itemHeight)
                )
            }
            item {
                SettingsListItem(
                    checked = true,
                    onSwitch = {},
                    title = stringResource(R.string.add_participants),
                    modifier = Modifier.fillMaxWidth().height(itemHeight)
                )
            }
            item {
                SettingsListItem(
                    checked = true,
                    onSwitch = {},
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
    onBack: () -> Unit,
    title: String
) {
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
