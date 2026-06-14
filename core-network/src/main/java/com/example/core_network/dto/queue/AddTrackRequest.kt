package com.example.core_network.dto.queue

import kotlinx.serialization.Serializable

@Serializable
data class AddTrackRequest(
    val url: String,
    val source: TrackSource
)
