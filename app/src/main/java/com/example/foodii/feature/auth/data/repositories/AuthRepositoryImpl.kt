package com.example.foodii.feature.auth.data.repositories

import com.example.foodii.core.network.FoodiiAPI
import com.example.foodii.core.network.UpdatePreferencesRequest
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
    private val authApi: AuthApi,
    private val foodiiApi: FoodiiAPI,
    private val localDataSource: AuthLocalDataSource
) : AuthRepository {

    override val authState: Flow<User?> = localDataSource.getUser()

    override suspend fun login(username: String, password: String, fcmToken: String?): Result<User> {
        return try {
            val response = authApi.login(LoginRequest(username, password, fcmToken))
            val user = response.toDomain()
            if (user.id.isNotEmpty()) {
                localDataSource.saveUser(user)
                Result.success(user)
            } else {
                Result.failure(Exception("Error al mapear usuario"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun register(username: String, password: String, preferences: List<String>): Result<User> {
        return try {
            val response = authApi.register(RegisterRequest(username, password, preferences))
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

    override suspend fun updatePreferences(preferences: List<String>?, fcmToken: String?): Result<User> {
        return try {
            val response = foodiiApi.updatePreferences(UpdatePreferencesRequest(preferences, fcmToken))
            
            // IMPORTANTE: Obtenemos el usuario que ya tenemos guardado
            val currentUser = getCurrentUser()
            
            if (response.success == true && currentUser != null) {
                // NO usamos response.toDomain() aquí porque sabemos que viene nulo.
                // Actualizamos el usuario existente con los nuevos datos
                val updatedUser = currentUser.copy(
                    notificationCategoryPreferences = preferences,
                    fcmToken = fcmToken ?: currentUser.fcmToken
                )
                localDataSource.saveUser(updatedUser)
                Result.success(updatedUser)
            } else if (currentUser != null) {
                Result.success(currentUser)
            } else {
                Result.failure(Exception("No se encontró una sesión activa"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
