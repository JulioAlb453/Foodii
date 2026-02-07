package com.example.foodii.feature.auth.data.repositories

import android.util.Log
import com.example.foodii.feature.auth.data.datasource.local.AuthLocalDataSource
import com.example.foodii.feature.auth.data.datasource.remote.AuthApi
import com.example.foodii.feature.auth.data.datasource.remote.LoginRequest
import com.example.foodii.feature.auth.data.datasource.remote.mapper.toDomain
import com.example.foodii.feature.auth.domain.entity.User
import com.example.foodii.feature.auth.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class AuthRepositoryImpl(
    private val api: AuthApi,
    private val localDataSource: AuthLocalDataSource
) : AuthRepository {

    override val authState: Flow<User?> = localDataSource.getUser()

    override suspend fun login(username: String, password: String): Result<User> {
        return try {
            val response = api.login(LoginRequest(username, password))
            val user = response.toDomain()
            
            if (user.token.isNullOrEmpty()) {
                Log.e("AWS_AUTH", "Error: La API de login no devolvió un token válido")
                return Result.failure(Exception("Token no recibido del servidor"))
            }

            Log.d("AWS_AUTH", "Login exitoso. Guardando token para el usuario: ${user.id}")
            localDataSource.saveUser(user)
            Result.success(user)
        } catch (e: Exception) {
            Log.e("AWS_AUTH", "Error en login: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun register(username: String, password: String): Result<User> {
        return try {
            val response = api.register(LoginRequest(username, password))
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
