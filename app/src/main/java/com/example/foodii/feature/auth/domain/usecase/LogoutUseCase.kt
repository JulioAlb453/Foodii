package com.example.foodii.feature.auth.domain.usecase

import com.example.foodii.feature.auth.domain.repository.AuthRepository

class LogoutUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke() {
        repository.logout()
    }
}
