package com.example.foodii.feature.auth.domain.repository

import com.example.foodii.feature.auth.domain.entity.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val authState: Flow<User?>
    suspend fun login(username: String, password: String): Result<User>
    suspend fun register(username: String, password: String): Result<User>
    suspend fun logout()
    suspend fun getCurrentUser(): User?
}
