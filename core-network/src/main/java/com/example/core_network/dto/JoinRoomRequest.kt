package com.example.core_network.dto

import kotlinx.serialization.Serializable

@Serializable
data class JoinRoomRequest(
    val inviteCode: String
)
