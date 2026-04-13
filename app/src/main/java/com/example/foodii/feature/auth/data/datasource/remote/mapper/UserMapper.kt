package com.example.foodii.feature.auth.data.datasource.remote.mapper

import android.util.Log
import com.example.foodii.feature.auth.data.datasource.remote.model.AuthResponse
import com.example.foodii.feature.auth.data.datasource.remote.model.UserDto
import com.example.foodii.feature.auth.domain.entity.User
import com.google.gson.Gson


fun AuthResponse?.toDomain(): User {
    Log.d("DEBUG_LOGIN", "Respuesta completa de la API: $this")
    
    // 1. Intentamos obtener los datos del usuario de múltiples lugares (anidado o plano)
    val userData = this?.data?.user ?: this?.directUser ?: this?.let { 
        // Si el objeto 'data' mismo tiene los campos (perfil plano)
        if (it.data?.id != null) {
            UserDto(
                id = it.data.id,
                username = it.data.username,
                preferences = it.data.preferences,
                createdAt = null
            )
        } else null
    }
    
    val token = this?.data?.token ?: this?.directToken

    if (userData == null) {
        Log.e("DEBUG_LOGIN", "ERROR: No se encontró información de usuario en la respuesta")
        return User(id = "", username = "", token = null)
    }

    Log.d("DEBUG_LOGIN", "Procesando usuario - id: ${userData.id}, username: ${userData.username}")

    // 2. Conversión segura del ID (String/UUID o Int)
    val userId = when (val rawId = userData.id) {
        is Double -> rawId.toInt().toString()
        is Int -> rawId.toString()
        is Long -> rawId.toString()
        else -> rawId?.toString() ?: ""
    }

    // 3. Procesamiento flexible de preferencias (Array o JSON String)
    val preferences = when (val rawPrefs = userData.preferences) {
        is List<*> -> rawPrefs.filterIsInstance<String>()
        is String -> {
            try {
                Gson().fromJson(rawPrefs, Array<String>::class.java).toList()
            } catch (e: Exception) {
                rawPrefs.split(",").map { it.trim() }.filter { it.isNotEmpty() }
            }
        }
        else -> null
    }

    Log.d("DEBUG_LOGIN", "Mapeo final exitoso - ID: $userId, Prefs: $preferences")

    return User(
        id = userId,
        username = userData.username ?: "Usuario",
        token = token,
        notificationCategoryPreferences = preferences
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
