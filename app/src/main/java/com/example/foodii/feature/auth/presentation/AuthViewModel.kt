package com.example.foodii.feature.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodii.feature.auth.domain.usecase.LoginUseCase
import com.example.foodii.feature.auth.domain.usecase.RegisterUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState = _uiState.asStateFlow()

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = loginUseCase(username, password)
            _uiState.update { currentState ->
                result.fold(
                    onSuccess = { user ->
                        currentState.copy(isLoading = false, user = user, isSuccess = true)
                    },
                    onFailure = { error ->
                        currentState.copy(isLoading = false, error = error.message)
                    }
                )
            }
        }
    }

    fun register(username: String, password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = registerUseCase(username, password)
            _uiState.update { currentState ->
                result.fold(
                    onSuccess = { user ->
                        currentState.copy(isLoading = false, user = user, isSuccess = true)
                    },
                    onFailure = { error ->
                        currentState.copy(isLoading = false, error = error.message)
                    }
                )
            }
        }
    }
    
    fun resetError() {
        _uiState.update { it.copy(error = null) }
    }
}
