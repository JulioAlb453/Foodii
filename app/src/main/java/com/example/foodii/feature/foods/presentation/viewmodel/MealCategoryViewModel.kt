package com.example.foodii.feature.foods.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodii.feature.foods.domain.usecases.GetMelCategoryUseCase
import com.example.foodii.feature.foods.presentation.screen.CategoryUIState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MealCategoryViewModel (private val getCategory: GetMelCategoryUseCase): ViewModel() {
    private val _uiState = MutableStateFlow(CategoryUIState())
    val uiState = _uiState.asStateFlow()

    init {
        mealLoadCategory()
    }

    private fun mealLoadCategory() {
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            try {
                val result = getCategory()
                _uiState.update { currentState ->
                    result.fold(
                        onSuccess = { list ->
                            currentState.copy(isLoading = false, post = list)
                        },
                        onFailure = { error ->
                            currentState.copy(isLoading = false, error = error.message ?: "Error desconocido")
                        }
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.localizedMessage) }
            }
        }
    }
}



