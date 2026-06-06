package com.example.core_network.service

import com.example.core_network.dto.CreateRoomRequest
import com.example.core_network.dto.JoinRoomRequest
import com.example.core_network.dto.RoomResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface RoomService {
    @POST("api/rooms")
    fun createRoom(@Body request: CreateRoomRequest) : Response<RoomResponse>

    @POST("api/rooms/join")
    fun joinRoom(@Body request: JoinRoomRequest) : Response<RoomResponse>

    @GET("api/rooms/{roomId}")
    fun getRoom(@Path("roomId") roomId: String) : Response<RoomResponse>
}
