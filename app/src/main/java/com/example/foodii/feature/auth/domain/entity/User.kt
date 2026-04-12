package com.example.foodii.feature.auth.domain.entity

import java.util.Date

/**
 * Modelo de usuario actualizado para incluir preferencias de comida.
 */
data class User(
    val id: String,
    val username: String,
    val password: String = "",
    val fcmToken: String? = null,
    val notificationCategoryPreferences: List<String>? = null,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date(),
    val token: String? = null // Manteniendo compatibilidad con el código existente
)
