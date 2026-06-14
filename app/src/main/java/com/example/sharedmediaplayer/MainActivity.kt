package com.example.sharedmediaplayer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.auth.ui.AuthDestination
import com.example.auth.ui.AuthNavHost
import com.example.room.ui.RoomTopBar
import com.example.settings.ui.SettingsTopBar
import com.example.room.ui.RoomDestination
import com.example.room.ui.RoomScreen
import com.example.settings.ui.Settings
import com.example.settings.ui.SettingsDestination
import com.example.core_ui.theme.SharedMediaPlayerTheme
import com.example.hello.HelloDestination
import com.example.hello.HelloScreen
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
                        .fillMaxSize(),
                    topBar = {
                        val currentBackStackEntry = navController.currentBackStackEntryAsState().value
                        val currentScreen = currentBackStackEntry?.destination?.route

                        when (currentScreen) {
                            RoomDestination::class.qualifiedName -> {
                                RoomTopBar(
                                    onBack = {
                                        navController.navigate(route = HelloDestination) {
                                            popUpTo(HelloDestination) {
                                                inclusive = true
                                            }
                                            launchSingleTop = true
                                        }
                                    },
                                    onSettings = {
                                        navController.navigate(route = SettingsDestination())
                                    },
                                    title = ""
                                )
                            }
                            SettingsDestination::class.qualifiedName -> {
                                SettingsTopBar(
                                    onBack = {
                                        navController.navigate(route = RoomDestination) {
                                            popUpTo(RoomDestination) {
                                                inclusive = false
                                            }
                                            launchSingleTop = true
                                        }
                                    },
                                    title = ""
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = AuthDestination,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable<AuthDestination> {
                            AuthNavHost(
                                toMainScreen = {
                                    navController.navigate(route = HelloDestination) {
                                        popUpTo(route = AuthDestination) {
                                            inclusive = true
                                        }
                                        launchSingleTop = true
                                    }
                                }
                            )
                        }

                        composable<HelloDestination> {
                            HelloScreen(
                                onCreateRoom = {
                                    navController.navigate(route = RoomDestination("Test room"))
                                },
                                onJoinRoom = {}
                            )
                        }

                        composable<RoomDestination> {
                            RoomScreen(
                                onBack = {
                                    navController.navigate(route = HelloDestination) {
                                        popUpTo(HelloDestination) {
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
