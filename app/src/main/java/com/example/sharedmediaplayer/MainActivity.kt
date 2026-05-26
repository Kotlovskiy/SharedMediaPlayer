package com.example.sharedmediaplayer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.sharedmediaplayer.room.RoomDestination
import com.example.sharedmediaplayer.room.RoomScreen
import com.example.sharedmediaplayer.settings.Settings
import com.example.sharedmediaplayer.settings.SettingsDestination
import com.example.sharedmediaplayer.settings.SettingsTopBar
import com.example.sharedmediaplayer.ui.theme.SharedMediaPlayerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SharedMediaPlayerTheme {
                val navController = rememberNavController()

                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Main,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable<Main> {
                            MainScreen(
                                onCreateRoom = {
                                    navController.navigate(route = RoomDestination("Test room"))
                                },
                                onJoinRoom = {}
                            )
                        }

                        composable<RoomDestination> { backStackEntry ->
                            val room: RoomDestination = backStackEntry.toRoute()

                            RoomScreen(
                                onBack = {
                                    navController.navigate(route = Main) {
                                        popUpTo(Main) {
                                            inclusive = true
                                        }
                                        launchSingleTop = true
                                    }
                                },
                                onSettings = {
                                    navController.navigate(route = SettingsDestination())
                                },
                            )
                        }

                        composable<SettingsDestination> { backStackEntry ->
                            Settings(
                                onBack = { navController.safePopBackStack(backStackEntry) },
                            )
                        }
                    }
                }
            }
        }
    }

    fun NavBackStackEntry.isResumed(): Boolean {
        return this.lifecycle.currentState == Lifecycle.State.RESUMED
    }

    fun NavHostController.safePopBackStack(entry: NavBackStackEntry): Boolean {
        return if (entry.isResumed()) {
            popBackStack()
            true
        } else {
            false
        }
    }
}
