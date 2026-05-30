package com.example.auth

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.auth.enter.Enter
import com.example.auth.enter.EnterDestination
import com.example.auth.registration.RegistrationDestination

@Composable
fun AuthNavHost() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = EnterDestination,
        modifier = Modifier.fillMaxSize()
    ) {
        composable<EnterDestination> {
            Enter(
                modifier = Modifier.fillMaxSize()
            )
        }

        composable<RegistrationDestination> {

        }
    }
}
