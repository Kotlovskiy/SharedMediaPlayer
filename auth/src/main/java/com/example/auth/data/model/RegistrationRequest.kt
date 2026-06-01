package com.example.auth.data.model

import kotlinx.serialization.Serializable

@Serializable
data class RegistrationRequest(
    val email: String,
    val nickname: String,
    val password: String
)
