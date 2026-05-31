package com.example.auth.enter

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
fun Enter(
    toMainScreen: () -> Unit,
    toRegistration: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EnterViewModel = hiltViewModel()
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(Unit) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.actions.collect { action ->
                when (action) {
                    NavAction.ToMainScreen -> toMainScreen.invoke()
                    NavAction.ToRegistration -> toRegistration.invoke()
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

        Text(text = stringResource(R.string.auth))

        TextField(
            value = state.value.email,
            onValueChange = { newValue -> viewModel.emit(EnterIntent.SetEmail(newValue)) },
            placeholder = { Text(text = stringResource(R.string.email)) }
        )

        TextField(
            value = state.value.password,
            onValueChange = { newValue -> viewModel.emit(EnterIntent.SetPassword(newValue)) },
            placeholder = { Text(text = stringResource(R.string.password)) }
        )

        Button(
            onClick = { viewModel.emit(EnterIntent.ClickEnter) }
        ) {
            Text(
                text = stringResource(R.string.enter)
            )
        }

        Button(
            onClick = { viewModel.emit(EnterIntent.ClickToRegistration) }
        ) {
            Text(
                text = stringResource(R.string.registration)
            )
        }
    }
}
