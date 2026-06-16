package com.example.auth.data

import com.example.core_network.ApiResult
import com.example.core_network.dto.AuthRequest
import com.example.core_network.dto.RegistrationRequest
import com.example.core_network.service.AuthService
import com.example.core_network.toApiResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject

class AuthDataSource @Inject constructor(
    private val authService: AuthService
) {
    suspend fun login(
        email: String,
        password: String
    ) = withContext(Dispatchers.IO) {
        try {
            authService.login(
                AuthRequest(
                    email = email,
                    password = password
                )
            ).toApiResult()
        } catch (e: IOException) {
            ApiResult.NetworkException(e)
        }
    }

    suspend fun register(
        nickname: String,
        email: String,
        password: String
    ) = withContext(Dispatchers.IO) {
        try {
            authService.register(
                RegistrationRequest(
                    nickname = nickname,
                    email = email,
                    password = password
                )
            ).toApiResult()
        } catch (e: IOException) {
            ApiResult.NetworkException(e)
        }
    }
}
