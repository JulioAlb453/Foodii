package com.example.foodii.feature.auth.data.datasource.remote.mapper

import android.util.Log
import com.example.foodii.feature.auth.data.datasource.remote.model.AuthResponse
import com.example.foodii.feature.auth.data.datasource.remote.model.UserDto
import com.example.foodii.feature.auth.domain.entity.User


fun AuthResponse?.toDomain(): User {
    Log.d("DEBUG_LOGIN", "Respuesta completa de la API: $this")
    
    val userData = this?.data?.user ?: this?.directUser
    val token = this?.data?.token ?: this?.directToken

    if (userData == null) {
        Log.e("DEBUG_LOGIN", "ERROR: No se encontró el objeto usuario (userData es null)")
        return User(id = "", username = "", token = null)
    }

    Log.d("DEBUG_LOGIN", "Datos del usuario recibidos - id: ${userData.id}, username: ${userData.username}")

    // Convertimos el ID a String de forma segura para MySQL
    val userId = when (val rawId = userData.id) {
        is Double -> rawId.toInt().toString()
        is Int -> rawId.toString()
        is Long -> rawId.toString()
        else -> rawId?.toString() ?: ""
    }

    if (userId.isBlank()) {
        Log.e("DEBUG_LOGIN", "ADVERTENCIA: El userId se mapeó como vacío. Revisa el nombre del campo en el JSON.")
    }

    return User(
        id = userId,
        username = userData.username ?: "Usuario",
        token = token,
        notificationCategoryPreferences = userData.preferences
    )
}

fun User.toDto(): UserDto {
    return UserDto(
        id = this.id,
        username = this.username,
        preferences = this.notificationCategoryPreferences,
        createdAt = null
    )
}
