package com.example.sharedmediaplayer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import com.example.auth.ui.AuthDestination
import com.example.auth.ui.AuthNavHost
import com.example.core_ui.AppTopBarParams
import com.example.room.ui.RoomTopBar
import com.example.settings.ui.SettingsTopBar
import com.example.room.ui.RoomDestination
import com.example.room.ui.RoomScreen
import com.example.settings.ui.Settings
import com.example.settings.ui.SettingsDestination
import com.example.core_ui.theme.SharedMediaPlayerTheme
import com.example.hello.ui.HelloDestination
import com.example.hello.ui.HelloScreen
import com.example.hello.ui.dialog.create.CreateDialog
import com.example.hello.ui.dialog.create.CreateRoomDialog
import com.example.hello.ui.dialog.join.JoinDialog
import com.example.hello.ui.dialog.join.JoinRoomDialog
import com.example.room.ui.RoomTopBarParams
import com.example.settings.ui.SettingsTopBarParams
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MainContent()
        }
    }
}

@Composable
fun MainContent() {
    SharedMediaPlayerTheme {
        val navController = rememberNavController()
        var currentTopBarParams by remember {
            mutableStateOf<List<AppTopBarParams>>(listOf())
        }
        val snackBarHostState = remember { SnackbarHostState() }

        Scaffold(
            modifier = Modifier
                .fillMaxSize(),
            topBar = {
                if(currentTopBarParams.isNotEmpty()) {
                    when(currentTopBarParams.last()) {
                        is RoomTopBarParams -> {
                            RoomTopBar(
                                onBack = {
                                    (currentTopBarParams.last() as RoomTopBarParams)
                                        .onExit.invoke()

                                    currentTopBarParams = currentTopBarParams.dropLast(1)

                                    navController.navigate(route = HelloDestination) {
                                        popUpTo(HelloDestination) {
                                            inclusive = false
                                        }
                                        launchSingleTop = true
                                    }
                                },
                                onSettings = {
                                    navController.navigate(route = SettingsDestination())
                                },
                                title =
                                    (currentTopBarParams.last() as RoomTopBarParams)
                                        .roomName,
                                showSettingsButton =
                                    (currentTopBarParams.last() as RoomTopBarParams)
                                        .showSettingsButton
                            )
                        }
                        is SettingsTopBarParams -> {
                            SettingsTopBar(
                                onBack = {
                                    navController.navigateUp()
                                    currentTopBarParams = currentTopBarParams.dropLast(1)
                                }
                            )
                        }
                        else -> {}
                    }
                }
            },
            snackbarHost = {
                SnackbarHost(hostState = snackBarHostState)
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
                        },
                        showError = { error -> snackBarHostState.showSnackbar(error) }
                    )
                }

                composable<HelloDestination> {
                    HelloScreen(
                        onCreateRoom = {
                            navController.navigate(route = CreateDialog)
                        },
                        onJoinRoom = {
                            navController.navigate(route = JoinDialog)
                        }
                    )
                }

                composable<RoomDestination> {
                    RoomScreen(
                        setTopBarParams = { params ->
                            currentTopBarParams = currentTopBarParams + params
                        },
                        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                        showError = { error -> snackBarHostState.showSnackbar(error) }
                    )
                }

                composable<SettingsDestination> {
                    Settings(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)
                    )
                    currentTopBarParams = currentTopBarParams + SettingsTopBarParams
                }

                dialog<CreateDialog> {
                    CreateRoomDialog(
                        onConfirm = { id, name ->
                            navController.navigate(
                                route = RoomDestination(
                                    roomId = id,
                                    roomName = name
                                )
                            ) {
                                popUpTo(route = HelloDestination) { inclusive = false }
                            }
                        },
                        showError = { error ->
                            snackBarHostState.showSnackbar(error)
                        }
                    )
                }

                dialog<JoinDialog> {
                    JoinRoomDialog(
                        onConfirm = { id, name ->
                            navController.navigate(
                                route = RoomDestination(
                                    roomId = id,
                                    roomName = name
                                )
                            ) {
                                popUpTo(route = HelloDestination) { inclusive = false }
                            }
                        },
                        showError = { error ->
                            snackBarHostState.showSnackbar(error)
                        }
                    )
                }
            }
        }
    }
}
