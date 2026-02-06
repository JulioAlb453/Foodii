package com.example.foodii.feature.apifoodii.meal.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodii.feature.apifoodii.meal.domain.entity.FoodiiMealTime
import com.example.foodii.feature.apifoodii.meal.domain.usecase.SaveFoodiiMealUseCase
import com.example.foodii.feature.foods.presentation.screen.MealFoodiiDetailsUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

class MealFoodiiViewModel(
    private val saveFoodiiMealUseCase: SaveFoodiiMealUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MealFoodiiDetailsUiState())
    val uiState = _uiState.asStateFlow()

    fun saveMeal(
        name: String,
        date: LocalDate,
        mealTime: FoodiiMealTime,
        ingredients: List<Pair<String, Int>>,
        userId: String
    ) {
        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            try {
                val result = saveFoodiiMealUseCase(
                    name = name,
                    date = date,
                    mealTime = mealTime,
                    ingredientsRequest = ingredients,
                    userId = userId
                )

                _uiState.update { currentState ->
                    result.fold(
                        onSuccess = { meal ->
                            currentState.copy(isLoading = false, successData = meal)
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

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}