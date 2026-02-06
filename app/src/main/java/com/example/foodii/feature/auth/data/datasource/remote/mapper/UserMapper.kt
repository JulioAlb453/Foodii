package com.example.foodii.feature.auth.data.datasource.remote.mapper

import com.example.foodii.feature.auth.data.datasource.remote.model.AuthResponse
import com.example.foodii.feature.auth.data.datasource.remote.model.UserDto
import com.example.foodii.feature.auth.domain.entity.User

fun UserDto.toDomain(token: String? = null): User {
    return User(
        id = this.id ?: "",
        username = this.username ?: "",
        token = token
    )
}

fun AuthResponse.toDomain(): User {
    return this.user.toDomain(this.token)
}

fun User.toDto(): UserDto {
    return UserDto(
        id = this.id,
        username = this.username,
        created_at = null,
        updated_at = null
    )
}
