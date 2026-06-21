package com.example.room.data

import com.example.core_network.dto.RoomResponse
import com.example.core_network.dto.UserResponse
import com.example.core_network.dto.queue.TrackResponse
import com.example.room.domain.Participant
import com.example.room.domain.Room
import com.example.room.domain.Song

fun RoomResponse.toRoom() =
    Room(
        id = id,
        name = name,
        inviteCode = inviteCode,
        participants = participants.toParticipants()
    )

fun UserResponse.toParticipant() =
    Participant(
        id = id,
        name = username
    )

fun List<UserResponse>.toParticipants() = map { it.toParticipant() }

fun TrackResponse.toSong() =
    Song(
        id = id,
        url = streamUrl,
        title = name,
        artist = artist
    )

fun List<TrackResponse>.toSongs() = map { it.toSong() }
