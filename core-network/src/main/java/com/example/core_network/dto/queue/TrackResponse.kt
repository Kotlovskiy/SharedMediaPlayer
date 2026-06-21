package com.example.core_network.dto.queue

import kotlinx.serialization.Serializable

@Serializable
data class TrackResponse(
    val id: String,
    val name: String,
    val artist: String,
    val durationSec: Int,
    val source: TrackSource,
    val streamUrl: String?,
    val streamUrlExpiresAt: String?,
    val position: Int,
    val addedBy: String
)
