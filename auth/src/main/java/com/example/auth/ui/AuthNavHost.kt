package com.example.auth.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.auth.ui.enter.Enter
import com.example.auth.ui.enter.EnterDestination

@Composable
fun AuthNavHost(
    toMainScreen: () -> Unit
) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = EnterDestination,
        modifier = Modifier.fillMaxSize()
    ) {
        composable<EnterDestination> {
            Enter(
                toMainScreen = toMainScreen,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}
