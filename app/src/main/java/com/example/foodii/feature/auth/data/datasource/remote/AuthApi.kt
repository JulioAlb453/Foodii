package com.example.foodii.feature.auth.data.datasource.remote

import com.example.foodii.feature.auth.data.datasource.remote.model.AuthResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("/api/auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse
}


data class LoginRequest(
    val username: String,
    val password: String
)

data class RegisterRequest(
    val username: String,
    val password: String,
    val notificationCategoryPreferences: List<String>? = null
)
