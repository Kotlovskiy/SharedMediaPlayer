package com.example.core_network.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    val id: String,
    val username: String
)
