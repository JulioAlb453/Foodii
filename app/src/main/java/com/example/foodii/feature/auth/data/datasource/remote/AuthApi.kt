package com.example.foodii.feature.auth.data.datasource.remote

import com.example.foodii.feature.auth.data.datasource.remote.model.AuthResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PATCH

interface AuthApi {
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("/api/auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @GET("api/auth/profile")
    suspend fun getProfile(
        @Header("Authorization") token: String? = null
    ): AuthResponse
}


data class LoginRequest(
    val username: String,
    val password: String,
    val fcmToken: String? = null
)

data class RegisterRequest(
    val username: String,
    val password: String,
    val notificationCategoryPreferences: List<String>? = null
)
