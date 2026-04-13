package com.example.foodii.feature.auth.data.datasource.remote.model

import com.google.gson.annotations.SerializedName

data class AuthResponse(
    @SerializedName("success") val success: Boolean?,
    @SerializedName("data") val data: AuthDataDto?,
    @SerializedName("user") val directUser: UserDto?,
    @SerializedName("token") val directToken: String?
)

data class AuthDataDto(
    @SerializedName("user") val user: UserDto?,
    @SerializedName("token") val token: String?
)

data class UserDto(
    // Aceptamos Any para que funcione si MySQL devuelve un número (1) o un string ("1")
    @SerializedName("id", alternate = ["userId", "_id", "ID"]) val id: Any?,
    @SerializedName("username") val username: String?,
    @SerializedName("notificationCategoryPreferences") val preferences: List<String>? = null,
    @SerializedName("createdAt") val createdAt: String?
)
