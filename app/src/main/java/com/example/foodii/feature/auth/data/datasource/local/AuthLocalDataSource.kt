package com.example.foodii.feature.auth.data.datasource.local

import com.example.foodii.feature.auth.domain.entity.User
import kotlinx.coroutines.flow.Flow

interface AuthLocalDataSource {
    suspend fun saveUser(user: User)
    fun getUser(): Flow<User?>
    suspend fun clearUser()
}
