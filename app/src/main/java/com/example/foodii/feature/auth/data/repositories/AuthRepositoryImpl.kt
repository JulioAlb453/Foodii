package com.example.foodii.feature.auth.data.repositories

import android.util.Log
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
            Log.d("AUTH_SYNC", "Iniciando login para: $username")
            val loginResponse = authApi.login(LoginRequest(username, password, fcmToken))
            val loginUser = loginResponse.toDomain()
            
            if (loginUser.id.isNotEmpty() && !loginUser.token.isNullOrEmpty()) {
                Log.d("AUTH_SYNC", "Login exitoso, obteniendo perfil para verificar categorías...")
                
                val profileResponse = authApi.getProfile("Bearer ${loginUser.token}")
                val profileUser = profileResponse.toDomain()
                
                Log.d("AUTH_SYNC", "Categorías recuperadas del servidor: ${profileUser.notificationCategoryPreferences}")

                val finalUser = profileUser.copy(
                    token = loginUser.token,
                    fcmToken = fcmToken ?: profileUser.fcmToken
                )
                
                localDataSource.saveUser(finalUser)
                Result.success(finalUser)
            } else {
                Result.failure(Exception("Credenciales inválidas"))
            }
        } catch (e: Exception) {
            Log.e("AUTH_SYNC", "Error durante el proceso de login/sync: ${e.message}")
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
            val currentUser = getCurrentUser()
            
            if (response.success == true && currentUser != null) {
                Log.d("AUTH_SYNC", "Preferencias actualizadas en servidor: $preferences")
                val updatedUser = currentUser.copy(
                    notificationCategoryPreferences = preferences ?: emptyList(),
                    fcmToken = fcmToken ?: currentUser.fcmToken
                )
                localDataSource.saveUser(updatedUser)
                Result.success(updatedUser)
            } else if (currentUser != null) {
                Result.success(currentUser)
            } else {
                Result.failure(Exception("Sesión no encontrada"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun syncProfile(): Result<User> {
        return try {
            val currentUser = getCurrentUser()
            val tokenHeader = if (currentUser?.token != null) "Bearer ${currentUser.token}" else null
            
            val response = authApi.getProfile(tokenHeader)
            val apiUser = response.toDomain()

            Log.d("AUTH_SYNC", "Sincronizando perfil. Categorías: ${apiUser.notificationCategoryPreferences}")

            if (currentUser != null && apiUser.id.isNotEmpty()) {
                val syncedUser = apiUser.copy(token = currentUser.token)
                localDataSource.saveUser(syncedUser)
                Result.success(syncedUser)
            } else if (apiUser.id.isNotEmpty()) {
                localDataSource.saveUser(apiUser)
                Result.success(apiUser)
            } else {
                Result.failure(Exception("Perfil vacío"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
