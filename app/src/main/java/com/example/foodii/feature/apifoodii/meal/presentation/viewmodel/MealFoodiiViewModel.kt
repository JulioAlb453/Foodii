package com.example.foodii.feature.apifoodii.meal.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodii.feature.apifoodii.meal.domain.entity.DailySummary
import com.example.foodii.feature.apifoodii.meal.domain.entity.FoodiiMeal
import com.example.foodii.feature.apifoodii.meal.domain.entity.FoodiiMealTime
import com.example.foodii.feature.apifoodii.meal.domain.usecase.GetMealsByDateRangeUseCase
import com.example.foodii.feature.apifoodii.meal.domain.usecase.GetMealsUseCase
import com.example.foodii.feature.apifoodii.meal.domain.usecase.SaveFoodiiMealUseCase
import com.example.foodii.feature.apifoodii.meal.presentation.screen.MealFoodiiDetailsUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

class MealFoodiiViewModel(
    private val saveFoodiiMealUseCase: SaveFoodiiMealUseCase,
    private val getMealsByDateRangeUseCase: GetMealsByDateRangeUseCase,
    private val getMealsUseCase: GetMealsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MealFoodiiDetailsUiState())
    val uiState = _uiState.asStateFlow()

    private val _summaries = MutableStateFlow<List<DailySummary>>(emptyList())
    val summaries = _summaries.asStateFlow()

    private val _allMeals = MutableStateFlow<List<FoodiiMeal>>(emptyList())
    val allMeals = _allMeals.asStateFlow()

    fun loadAllMeals(userId: String) {
        Log.d("MEAL_VM", "loadAllMeals llamado con userId: $userId")
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            try {
                getMealsUseCase(userId).collect { meals ->
                    Log.d("MEAL_VM", "Se recibieron ${meals.size} comidas de la API")
                    _allMeals.value = meals
                    _uiState.update { it.copy(isLoading = false) }
                }
            } catch (e: Exception) {
                Log.e("MEAL_VM", "Error al cargar comidas: ${e.localizedMessage}")
                _uiState.update { it.copy(isLoading = false, error = e.localizedMessage) }
            }
        }
    }

    fun loadMealsRange(userId: String, startDate: String, endDate: String) {
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            try {
                getMealsByDateRangeUseCase(userId, startDate, endDate).collect { data ->
                    _summaries.value = data
                    _uiState.update { it.copy(isLoading = false) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.localizedMessage) }
            }
        }
    }

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
                val result = saveFoodiiMealUseCase(name, date, mealTime, ingredients, userId)
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
