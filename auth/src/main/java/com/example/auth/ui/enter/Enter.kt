package com.example.auth.ui.enter

import android.app.Activity
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.core_ui.theme.Typography

@Composable
fun Enter(
    toMainScreen: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EnterViewModel = hiltViewModel()
) {

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
                    Log.i("errrrror", "error: ${it?.error}; message: ${it?.message}")
                }
            )
        }
    }

    LaunchedEffect(Unit) {
        try {
            viewModel.startAuth(authLauncher = authLauncher)
        } catch (e: Exception) {
            Log.i("in catch", e.message ?: "")
        }
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Пожалуйста, подождите", style = Typography.headlineMedium)
        Spacer(Modifier.height(32.dp))
    }
}
