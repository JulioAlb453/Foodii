package com.example.foodii.feature.auth.data.repositories

import android.util.Log
import com.example.foodii.feature.auth.data.datasource.local.AuthLocalDataSource
import com.example.foodii.feature.auth.data.datasource.remote.AuthApi
import com.example.foodii.feature.auth.data.datasource.remote.LoginRequest
import com.example.foodii.feature.auth.data.datasource.remote.RegisterRequest
import com.example.foodii.feature.auth.data.datasource.remote.mapper.toDomain
import com.example.foodii.feature.auth.domain.entity.User
import com.example.foodii.feature.auth.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val api: AuthApi,
    private val localDataSource: AuthLocalDataSource
) : AuthRepository {

    override val authState: Flow<User?> = localDataSource.getUser()

    override suspend fun login(username: String, password: String, fcmToken: String?): Result<User> {
        return try {
            val response = api.login(LoginRequest(username, password, fcmToken))
            val user = response.toDomain()
            localDataSource.saveUser(user)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun register(username: String, password: String, preferences: List<String>): Result<User> {
        return try {
            val response = api.register(RegisterRequest(username, password, preferences))
            val user = response.toDomain()
            if (!user.token.isNullOrEmpty()) {
                localDataSource.saveUser(user)
            }
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout() {
        localDataSource.clearUser()
    }

    override suspend fun getCurrentUser(): User? {
        return localDataSource.getUser().firstOrNull()
    }
}
