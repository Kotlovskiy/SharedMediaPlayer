package com.example.core_network.dto

import kotlinx.serialization.Serializable

@Serializable
data class CreateRoomRequest(
    val name: String
)
