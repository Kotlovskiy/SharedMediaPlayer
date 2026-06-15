package com.example.room.ui

import com.example.core_ui.AppTopBarParams

data class RoomTopBarParams(
    val roomId: String,
    val roomName: String,
    val showSettingsButton: Boolean,
    val onExit: () -> Unit
): AppTopBarParams()
