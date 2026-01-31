package com.example.foodii.feature.foods.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodii.feature.foods.presentation.screen.MealDetailsUIState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MealDetailsViewModel(
    private val getMealInstructionsUseCase: GetMealInstructionsUseCase,
    private val letter: String
) : ViewModel(){
    private val _uiState = MutableStateFlow(MealDetailsUIState())
    val uiState = _uiState.asStateFlow()

    init {
        loadMealDetails()
    }

    private fun loadMealDetails() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            val result = getMealInstructionsUseCase(letter)
            _uiState.update { currentState ->
                result.fold(
                    onSuccess = { list ->
                        currentState.copy(isLoading = false, meals = list)
                    },
                    onFailure = { error ->
                        currentState.copy(isLoading = false, error = error.message)
                    }
                )
            }
        }
    }
}