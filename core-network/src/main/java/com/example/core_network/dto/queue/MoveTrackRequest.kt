package com.example.core_network.dto.queue

import kotlinx.serialization.Serializable

@Serializable
data class MoveTrackRequest(
    val newPosition: Int
)
