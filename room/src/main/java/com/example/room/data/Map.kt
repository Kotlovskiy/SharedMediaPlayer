package com.example.room.data

import com.example.core_network.dto.RoomResponse
import com.example.core_network.dto.UserResponse
import com.example.room.domain.Participant
import com.example.room.domain.Room

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
