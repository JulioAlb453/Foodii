package com.example.foodii.feature.auth.presentation

import com.example.foodii.feature.auth.domain.entity.User


data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val user: User? = null,
    val isSuccess: Boolean = false
)
