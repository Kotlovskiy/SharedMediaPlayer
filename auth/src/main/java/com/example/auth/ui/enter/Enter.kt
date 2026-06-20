package com.example.auth.ui.enter

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import kotlinx.coroutines.runBlocking
import com.example.core_ui.R as coreR

@Composable
fun Enter(
    toMainScreen: () -> Unit,
    toRegistration: () -> Unit,
    showError: suspend (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EnterViewModel = hiltViewModel()
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val commonErrorText = stringResource(coreR.string.smt_went_wrong)
    val internetErrorText = stringResource(coreR.string.check_connection)
    val serverErrorText = stringResource(coreR.string.server_error)
    val invalidDataErrorText = stringResource(R.string.invalid_data)
    val forbiddenErrorText = stringResource(R.string.access_denied)

    LaunchedEffect(Unit) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.actions.collect { action ->
                when (action) {
                    AuthEffect.ToMainScreen -> toMainScreen.invoke()
                    AuthEffect.ToRegistration -> toRegistration.invoke()
                    AuthEffect.ShowForbiddenError -> showError(forbiddenErrorText)
                    AuthEffect.ShowInternetError -> showError(internetErrorText)
                    AuthEffect.ShowInvalidDataError -> showError(invalidDataErrorText)
                    AuthEffect.ShowServerError -> showError(serverErrorText)
                    AuthEffect.ShowUnknownError -> showError(commonErrorText)
                }
            }
        }
    }

    val authLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            viewModel.authManager.authorizationIntentHandler(
                data!!,
                onSuccess = {
                    toMainScreen()
                },
                onAuthError = {
                    runBlocking { showError("error: ${it?.error}; message: ${it?.message}") }
                }
            )
        }
    }

    try {
        //viewModel.authManager.startAuthorization(authLauncher)
    } catch (e: Exception) {
        runBlocking { showError(e.toString()) }
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
