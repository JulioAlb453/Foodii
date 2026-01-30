package com.example.foodii.feature.foods.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodii.feature.foods.domain.usecases.GetMelCategoryUseCase
import com.example.foodii.feature.foods.presentation.screen.CatetoryUIState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MealCategoryViewModel (private val getCategory: GetMelCategoryUseCase): ViewModel() {
    private val _uiState = MutableStateFlow(CatetoryUIState())
    val uiState = _uiState.asStateFlow()

    private fun mealLoadCategory (){
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            val result = getCategory()
            _uiState.update { currentState ->
                result.fold(
                    onSuccess = {list ->
                        currentState.copy(isLoading = false, post = list)
                    },
                    onFailure = {error ->
                        currentState.copy(isLoading = false, error = error.message)
                    }
                )
            }
        }
    }
}



