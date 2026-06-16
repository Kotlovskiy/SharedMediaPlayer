package com.example.hello.data

import com.example.core_network.dto.CreateRoomRequest
import com.example.core_network.dto.JoinRoomRequest
import com.example.core_network.service.RoomService
import com.example.core_network.toApiResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class InviteDataSource @Inject constructor(
    private val api: RoomService
) {
    suspend fun createRoom(roomName: String) =
        withContext(Dispatchers.IO) {
            api.createRoom(CreateRoomRequest(roomName)).toApiResult()
        }


    suspend fun joinRoom(inviteCode: String) =
        withContext(Dispatchers.IO) {
            api.joinRoom(JoinRoomRequest(inviteCode)).toApiResult()
        }
}
