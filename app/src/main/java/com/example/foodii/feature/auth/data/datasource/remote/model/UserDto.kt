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
    @SerializedName("token") val token: String?,
    // Soporte para perfil plano (MySQL/Express común)
    @SerializedName("id", alternate = ["userId", "_id", "ID"]) val id: Any? = null,
    @SerializedName("username") val username: String? = null,
    @SerializedName("notificationCategoryPreferences", alternate = ["preferences"]) val preferences: Any? = null
)

data class UserDto(
    @SerializedName("id", alternate = ["userId", "_id", "ID"]) val id: Any?,
    @SerializedName("username") val username: String?,
    @SerializedName("notificationCategoryPreferences", alternate = ["preferences", "category_preferences", "categoryPreferences"]) val preferences: Any? = null,
    @SerializedName("createdAt") val createdAt: String?
)
