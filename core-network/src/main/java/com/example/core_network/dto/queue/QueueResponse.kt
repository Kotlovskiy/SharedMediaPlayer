package com.example.core_network.dto.queue

import kotlinx.serialization.Serializable

@Serializable
data class QueueResponse(
    val roomId: String,
    val currentTrackPosition: Int?,
    val tracks: List<TrackResponse>
)
