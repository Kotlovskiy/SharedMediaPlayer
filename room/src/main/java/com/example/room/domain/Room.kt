package com.example.room.domain

data class Room(
    val id: String,
    val name: String,
    val inviteCode: String,
    val participants: List<Participant>
)
