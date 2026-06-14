package com.example.core_network.dto.queue

import kotlinx.serialization.Serializable

@Serializable
data class TrackResponse(
    val id: String,
    val roomId: String,
    val name: String,
    val artist: String,
    val durationSec: Int,
    val source: TrackSource,
    val externalId: String,
    val streamUrl: String,
    val streamUrlExpiresAt: String,
    val position: Int,
    val addedBy: String,
    val createdAt: String,
)
