package com.example.hello.ui.dialog.join

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
import com.example.hello.ui.dialog.Effect

@Composable
fun JoinRoomDialog(
    onConfirm: (String, String) -> Unit,
    showError: suspend (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: JoinRoomDialogViewModel = hiltViewModel()
) {
    val state = viewModel.state.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current

    val commonErrorText = stringResource(R.string.smt_went_wrong)
    val internetErrorText = stringResource(R.string.check_connection)
    val serverErrorText = stringResource(R.string.server_error)
    val notFoundErrorText = stringResource(com.example.hello.R.string.not_found)
    val forbiddenErrorText = stringResource(com.example.hello.R.string.access_denied)
    val conflictErrorText = stringResource(com.example.hello.R.string.conflict)
    val invalidDataErrorText = stringResource(com.example.hello.R.string.invalid_data)

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
                    Effect.ShowConflictError -> showError(conflictErrorText)
                    Effect.ShowInvalidDataError -> showError(invalidDataErrorText)
                    is Effect.NavigateToRoom -> onConfirm(effect.id, effect.name)
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
            text = stringResource(com.example.hello.R.string.input_join_key)
        )

        TextField(
            value = state.value,
            onValueChange = { newValue -> viewModel.setInviteCode(newValue) },
            placeholder = {
                Text(
                    text = stringResource(com.example.hello.R.string.join_key)
                )
            }
        )

        Button(
            onClick = { viewModel.clickConfirm() },
        ) {
            Text(
                text = stringResource(com.example.hello.R.string.confirm)
            )
        }
    }
}
