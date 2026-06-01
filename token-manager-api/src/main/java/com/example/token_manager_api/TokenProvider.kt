package com.example.token_manager_api

interface TokenProvider {
    fun getAccessToken(): String?
    fun getRefreshToken(): String?
    fun isAccessTokenValid(): Boolean
    fun isRefreshTokenValid(): Boolean
}
