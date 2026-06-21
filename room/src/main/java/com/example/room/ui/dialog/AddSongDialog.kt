package com.example.room.ui.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.example.core_ui.R

@Composable
fun AddSongDialog(
    onConfirm: () -> Unit,
    showError: suspend (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AddSongDialogViewModel = hiltViewModel()
) {
    val state = viewModel.state.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current

    val commonErrorText = stringResource(R.string.smt_went_wrong)
    val internetErrorText = stringResource(R.string.check_connection)
    val serverErrorText = stringResource(R.string.server_error)
    val notFoundErrorText = stringResource(com.example.room.R.string.add_song_not_found_error)
    val forbiddenErrorText = stringResource(com.example.room.R.string.add_song_access_denied_error)
    val invalidDataErrorText = stringResource(com.example.room.R.string.add_song_invalid_data_error)

    LaunchedEffect(Unit) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.effect.collect { effect ->
                when(effect) {
                    Effect.ShowUnknownError -> showError(commonErrorText)
                    Effect.ShowInternetError -> showError(internetErrorText)
                    Effect.ShowServerError -> showError(serverErrorText)
                    Effect.UnauthorizedError -> {/*onLogoutAction()*/}
                    Effect.ShowForbiddenError -> showError(forbiddenErrorText)
                    Effect.ShowNotFoundError -> showError(notFoundErrorText)
                    Effect.ShowConflictError -> showError(commonErrorText)
                    Effect.ShowInvalidDataError -> showError(invalidDataErrorText)
                    Effect.SongWasAdded -> onConfirm()
                }
            }
        }
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)
    ) {
        Text(
            text = stringResource(com.example.room.R.string.add_song_title)
        )

        TextField(
            value = state.value,
            onValueChange = { newValue -> viewModel.setUrl(newValue) },
            placeholder = {
                Text(
                    text = stringResource(com.example.room.R.string.add_song_placeholder)
                )
            }
        )

        Button(
            onClick = { viewModel.clickConfirm() },
        ) {
            Text(
                text = stringResource(com.example.room.R.string.confirm)
            )
        }
    }
}

