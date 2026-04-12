package com.example.foodii.feature.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodii.feature.auth.domain.usecase.LoginUseCase
import com.example.foodii.feature.auth.domain.usecase.LogoutUseCase
import com.example.foodii.feature.auth.domain.usecase.RegisterUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val logoutUseCase: LogoutUseCase
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

    /**
     * Realiza el registro con preferencias y, si es exitoso, realiza el login automáticamente.
     */
    fun registerThenLogin(
        username: String, 
        password: String, 
        preferences: List<String> = emptyList()
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            // 1. Registro enviando las preferencias seleccionadas
            val registerResult = registerUseCase(username, password, preferences)
            
            registerResult.fold(
                onSuccess = {
                    // 2. Login automático tras registro exitoso para obtener el token
                    val loginResult = loginUseCase(username, password)
                    _uiState.update { currentState ->
                        loginResult.fold(
                            onSuccess = { user ->
                                currentState.copy(isLoading = false, user = user, isSuccess = true)
                            },
                            onFailure = { error ->
                                currentState.copy(isLoading = false, error = "Registro exitoso, pero error al iniciar sesión: ${error.message}")
                            }
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message) }
                }
            )
        }
    }

    fun logout() {
        viewModelScope.launch {
            logoutUseCase()
            _uiState.update { AuthUiState() }
        }
    }
    
    fun resetError() {
        _uiState.update { it.copy(error = null) }
    }
}
