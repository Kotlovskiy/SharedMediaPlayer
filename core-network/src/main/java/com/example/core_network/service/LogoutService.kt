package com.example.core_network.service

import retrofit2.http.DELETE

interface LogoutService {
    @DELETE("logout")
    suspend fun logout()
}
