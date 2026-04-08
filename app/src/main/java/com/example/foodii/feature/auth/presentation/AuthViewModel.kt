package com.example.foodii.feature.auth.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodii.feature.auth.domain.usecase.LoginUseCase
import com.example.foodii.feature.auth.domain.usecase.LogoutUseCase
import com.example.foodii.feature.auth.domain.usecase.RegisterUseCase
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

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
            
            val fcmToken = getFcmToken()
            Log.d("AuthViewModel", "FCM Token obtenido: $fcmToken")
            
            val result = loginUseCase(username, password, fcmToken)
            
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

    private suspend fun getFcmToken(): String? = suspendCancellableCoroutine { continuation ->
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                continuation.resume(task.result)
            } else {
                Log.e("AuthViewModel", "Error al obtener FCM token", task.exception)
                continuation.resume(null)
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
