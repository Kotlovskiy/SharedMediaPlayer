package com.example.auth.ui.enter

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core_network.AuthManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EnterViewModel @Inject constructor(
    val authManager: AuthManager
) : ViewModel() {

    fun startAuth(
        authLauncher: ActivityResultLauncher<Intent>,
        onError: (Exception) -> Unit
    ) {
        viewModelScope.launch {
            try {
                authManager.startAuthorization(authLauncher)
            } catch (e: Exception) {
                onError(e)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        authManager.dispose()
    }
}
