package com.example.auth.registration

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
import com.example.auth.R

@Composable
fun Registration(
    toMainScreen: () -> Unit,
    toEnter: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RegistrationViewModel = hiltViewModel()
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(Unit) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.actions.collect { action ->
                when (action) {
                    NavAction.ToEnter -> toEnter.invoke()
                    NavAction.ToMainScreen -> toMainScreen.invoke()
                }
            }
        }
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
    ) {
        val state = viewModel.uiState.collectAsStateWithLifecycle()

        Text(text = stringResource(R.string.registration))

        TextField(
            value = state.value.email,
            onValueChange = { newValue ->
                viewModel.emit(RegistrationIntent.SetEmail(newValue))
            },
            placeholder = { Text(text = stringResource(R.string.email)) }
        )

        TextField(
            value = state.value.nickname,
            onValueChange = { newValue ->
                viewModel.emit(RegistrationIntent.SetNickname(newValue))
            },
            placeholder = { Text(text = stringResource(R.string.nickname)) }
        )

        TextField(
            value = state.value.password,
            onValueChange = { newValue ->
                viewModel.emit(RegistrationIntent.SetPassword(newValue))
            },
            placeholder = { Text(text = stringResource(R.string.password)) }
        )

        Button(
            onClick = { viewModel.emit(RegistrationIntent.ClickRegister) }
        ) {
            Text(
                text = stringResource(R.string.register)
            )
        }

        Button(
            onClick = { viewModel.emit(RegistrationIntent.ClickToEnter) }
        ) {
            Text(
                text = stringResource(R.string.auth)
            )
        }
    }
}
