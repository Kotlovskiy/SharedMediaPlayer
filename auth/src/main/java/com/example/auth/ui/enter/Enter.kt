package com.example.auth.ui.enter

import android.app.Activity
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.auth.R
import com.example.core_ui.theme.Typography

@Composable
fun Enter(
    toMainScreen: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EnterViewModel = hiltViewModel()
) {

    var openAuth by remember { mutableStateOf(true) }

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
                    openAuth = false
                }
            )
        } else if (result.resultCode == Activity.RESULT_CANCELED) {
            openAuth = false
        }
    }

    LaunchedEffect(openAuth) {
        if (openAuth) {
            try {
                viewModel.startAuth(
                    authLauncher = authLauncher,
                    onError = { error ->
                        Log.e("Enter", "Auth error: ${error.message}")
                        openAuth = false
                    }
                )
            } catch (_: Exception) {
                openAuth = false
            }
        }
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (openAuth) {
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                strokeWidth = 4.dp
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.wait_please),
                style = Typography.headlineSmall
            )
        }

        if(!openAuth) {
            Button(
                onClick = { openAuth = true }
            ) {
                Text(
                    text = stringResource(R.string.try_again),
                    style = Typography.labelMedium
                )
            }
        }
    }
}
