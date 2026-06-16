package com.example.hello.domain

import com.example.core_network.dto.RoomResponse

data class Room(
    val id: String,
    val name: String
)

fun RoomResponse.toRoom() =
    Room(
        id = id,
        name = name
    )
