package com.example.core_network.service

import com.example.core_network.dto.queue.AddTrackRequest
import com.example.core_network.dto.queue.DeleteResponse
import com.example.core_network.dto.queue.MoveTrackRequest
import com.example.core_network.dto.queue.QueueResponse
import com.example.core_network.dto.queue.TrackResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface QueueService {
    @POST("api/queue/{roomId}/tracks")
    suspend fun addSong(
        @Path("roomId") roomId: String,
        @Body request: AddTrackRequest
    ): Response<TrackResponse>

    @GET("api/queue/{roomId}")
    suspend fun getQueue(
        @Path("roomId") roomId: String
    ): Response<QueueResponse>

    @GET("api/queue/{roomId}/tracks/current")
    suspend fun getSong(
        @Path("roomId") roomId: String
    ): Response<TrackResponse>

    @PUT("api/queue/{roomId}/tracks/{trackId}/move")
    suspend fun moveSong(
        @Path("roomId") roomId: String,
        @Path("trackId") trackId: String,
        @Body moveTrackRequest: MoveTrackRequest
    ): Response<TrackResponse>

    @DELETE("api/queue/{roomId}/tracks/{trackId}")
    suspend fun deleteSong(
        @Path("roomId") roomId: String,
        @Path("trackId") trackId: String,
    ): Response<DeleteResponse>

    @DELETE("api/queue/{roomId}/tracks")
    suspend fun deleteAllSongs(
        @Path("roomId") roomId: String,
    ): Response<DeleteResponse>
}
