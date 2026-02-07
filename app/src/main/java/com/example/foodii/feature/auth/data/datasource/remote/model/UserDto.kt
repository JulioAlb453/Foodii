package com.example.foodii.feature.auth.data.datasource.remote.model

import com.google.gson.annotations.SerializedName

/**
 * Representa el JSON completo de respuesta
 */
data class AuthResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String?,
    @SerializedName("data") val data: AuthData?
)

/**
 * Representa el objeto "data" dentro de la respuesta
 */
data class AuthData(
    @SerializedName("user") val user: UserDto?,
    @SerializedName("token") val token: String?,
    @SerializedName("tokenExpiresIn") val tokenExpiresIn: String?
)

/**
 * Representa el objeto "user" dentro de "data"
 */
data class UserDto(
    @SerializedName("id") val id: String?,
    @SerializedName("username") val username: String?,
    @SerializedName("createdAt") val createdAt: String?
)
