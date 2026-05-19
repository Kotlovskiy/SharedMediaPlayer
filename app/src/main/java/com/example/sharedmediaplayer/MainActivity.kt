package com.example.sharedmediaplayer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.sharedmediaplayer.room.Room
import com.example.sharedmediaplayer.room.RoomTopBar
import com.example.sharedmediaplayer.room.RoomViewModel
import com.example.sharedmediaplayer.ui.theme.SharedMediaPlayerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SharedMediaPlayerTheme {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = Main
                ) {
                    composable<Main> {
                        Scaffold(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp)
                        ) { innerPadding ->
                            MainScreen(
                                modifier = Modifier.padding(innerPadding),
                                onCreateRoom = {
                                    navController.navigate(route = Room("Test room"))
                                },
                                onJoinRoom = {}
                            )
                        }
                    }

                    composable<Room> { backStackEntry ->
                        val room: Room = backStackEntry.toRoute()
                        val viewModel: RoomViewModel by viewModels()
                        Scaffold(
                            modifier = Modifier
                                .fillMaxSize(),
                            topBar = { RoomTopBar(
                                onBack = { navController.clearBackStack(route = Main) },
                                onSettings = { navController.popBackStack() },
                                title = room.name
                            ) }
                        ) { innerPadding ->
                            Room(
                                viewModel = viewModel,
                                modifier =  Modifier
                                    .padding(innerPadding)
                                    .padding(horizontal = 16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
