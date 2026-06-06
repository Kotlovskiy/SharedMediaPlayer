package com.example.core_network.dto

import kotlinx.serialization.Serializable

@Serializable
data class RegistrationRequest(
    val email: String,
    val nickname: String,
    val password: String
)
