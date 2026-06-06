package com.example.core_network.dto

import kotlinx.serialization.Serializable

@Serializable
data class RoomResponse(
    val id: String,
    val name: String,
    val inviteCode: String,
    val createdBy: String,
    val participants: List<UserResponse>,
    val createdAt: String
)
