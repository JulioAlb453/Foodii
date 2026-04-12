package com.example.foodii.feature.food_preferences.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodii.feature.auth.data.datasource.local.AuthLocalDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FoodPreferencesUiState(
    val selectedCategories: Set<String> = emptySet(),
    val isLoading: Boolean = false,
    val isSaved: Boolean = false
)

@HiltViewModel
class FoodPreferencesViewModel @Inject constructor(
    private val authLocalDataSource: AuthLocalDataSource
) : ViewModel() {

    private val _uiState = MutableStateFlow(FoodPreferencesUiState())
    val uiState: StateFlow<FoodPreferencesUiState> = _uiState.asStateFlow()

    val categories = listOf(
        "Fitness 💪",
        "Quesadillas 🌮",
        "Bajo en calorías 🥗",
        "Vegano 🌿",
        "Mariscos 🦐",
        "Antojitos 🌯",
        "Postres 🍰",
        "Internacional 🌎"
    )

    fun onCategoryToggled(categoryName: String) {
        val current = _uiState.value.selectedCategories
        val updated = if (current.contains(categoryName)) {
            current - categoryName
        } else {
            current + categoryName
        }
        _uiState.update { it.copy(selectedCategories = updated) }
    }

    fun savePreferencesLocally(onDone: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val currentUser = authLocalDataSource.getUser().firstOrNull()
            if (currentUser != null) {
                val updatedUser = currentUser.copy(
                    notificationCategoryPreferences = _uiState.value.selectedCategories.toList()
                )
                authLocalDataSource.saveUser(updatedUser)
            }

            _uiState.update { it.copy(isLoading = false, isSaved = true) }
            onDone()
        }
    }
}