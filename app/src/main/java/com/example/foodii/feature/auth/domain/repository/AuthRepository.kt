package com.example.foodii.feature.auth.domain.repository

import com.example.foodii.feature.auth.domain.entity.User

interface AuthRepository {
    suspend fun login(username: String, password: String): Result<User>
    suspend fun register(username: String, password: String): Result<User>
    suspend fun logout()
    suspend fun getCurrentUser(): User?
}
