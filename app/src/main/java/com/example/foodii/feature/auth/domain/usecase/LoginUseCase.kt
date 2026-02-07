package com.example.foodii.feature.auth.domain.usecase

import com.example.foodii.feature.auth.domain.entity.User
import com.example.foodii.feature.auth.domain.repository.AuthRepository

class LoginUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(username: String, password: String): Result<User> {
        if (username.isBlank() || password.isBlank()) {
            return Result.failure(Exception("El usuario y la contraseña no pueden estar vacíos"))
        }
        return repository.login(username, password)
    }
}
