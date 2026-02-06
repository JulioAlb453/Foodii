package com.example.foodii.feature.auth.data.repositories

import com.example.foodii.feature.auth.data.datasource.local.AuthLocalDataSource
import com.example.foodii.feature.auth.data.datasource.remote.AuthApi
import com.example.foodii.feature.auth.data.datasource.remote.LoginRequest
import com.example.foodii.feature.auth.data.datasource.remote.mapper.toDomain
import com.example.foodii.feature.auth.domain.entity.User
import com.example.foodii.feature.auth.domain.repository.AuthRepository
import kotlinx.coroutines.flow.firstOrNull

class AuthRepositoryImpl(
    private val api: AuthApi,
    private val localDataSource: AuthLocalDataSource // Dependencia para la sesión local
) : AuthRepository {

    override suspend fun login(username: String, password: String): Result<User> {
        return try {
            val response = api.login(LoginRequest(username, password))
            val user = response.toDomain()
            // Guardar sesión tras un login exitoso
            localDataSource.saveUser(user)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun register(username: String, password: String): Result<User> {
        return try {
            val response = api.register(LoginRequest(username, password))
            val user = response.toDomain()
            // Guardar sesión tras un registro exitoso
            localDataSource.saveUser(user)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Cierra la sesión actual borrando los datos persistidos localmente.
     */
    override suspend fun logout() {
        localDataSource.clearUser()
    }

    /**
     * Obtiene el usuario de la sesión guardada localmente.
     * Devuelve null si no hay ninguna sesión activa.
     */
    override suspend fun getCurrentUser(): User? {
        return localDataSource.getUser().firstOrNull()
    }
}
