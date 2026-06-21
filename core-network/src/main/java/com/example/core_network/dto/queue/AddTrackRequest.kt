package com.example.core_network.dto.queue

import kotlinx.serialization.Serializable

@Serializable
data class AddTrackRequest(
    val roomId: String,
    val youtubeUrl: String
)
