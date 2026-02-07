package com.example.foodii.feature.auth.domain.usecase

import com.example.foodii.feature.auth.domain.entity.User
import com.example.foodii.feature.auth.domain.repository.AuthRepository

class RegisterUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(username: String, password: String): Result<User> {
        if (username.length < 4 || password.length < 6) {
            return Result.failure(Exception("El usuario debe tener al menos 4 caracteres y la contraseña 6"))
        }
        return repository.register(username, password)
    }
}
