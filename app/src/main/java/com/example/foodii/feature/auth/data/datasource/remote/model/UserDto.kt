package com.example.foodii.feature.auth.data.datasource.remote.model

data class AuthResponse(
    val user: UserDto,
    val token: String?
)

data class UserDto(
    val id: String?,
    val username: String?,
    val created_at: Long?,
    val updated_at: Long?
)
