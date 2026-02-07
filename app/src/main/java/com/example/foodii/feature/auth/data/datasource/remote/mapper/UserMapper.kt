package com.example.foodii.feature.auth.data.datasource.remote.mapper

import com.example.foodii.feature.auth.data.datasource.remote.model.AuthResponse
import com.example.foodii.feature.auth.data.datasource.remote.model.UserDto
import com.example.foodii.feature.auth.domain.entity.User


fun AuthResponse?.toDomain(): User {
    // Si la respuesta es nula o no fue exitosa
    if (this?.data == null) {
        return User(id = "", username = "", token = null)
    }

    val userData = this.data.user
    val token = this.data.token

    return User(
        id = userData?.id ?: "",
        username = userData?.username ?: "Usuario",
        token = token
    )
}

fun User.toDto(): UserDto {
    return UserDto(
        id = this.id,
        username = this.username,
        createdAt = null // No es necesario enviar esto a la API
    )
}
