package com.example.foodii.feature.apifoodii.meal.presentation.viewmodel

import android.content.Context
import android.util.Log
import androidx.glance.appwidget.updateAll
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodii.core.hardware.domain.ShakeDetector
import com.example.foodii.feature.apifoodii.meal.domain.entity.DailySummary
import com.example.foodii.feature.apifoodii.meal.domain.entity.FoodiiMeal
import com.example.foodii.feature.apifoodii.meal.domain.entity.FoodiiMealTime
import com.example.foodii.feature.apifoodii.meal.domain.usecase.GetFoodiiMealByIdUseCase
import com.example.foodii.feature.apifoodii.meal.domain.usecase.GetMealsByDateRangeUseCase
import com.example.foodii.feature.apifoodii.meal.domain.usecase.GetMealsUseCase
import com.example.foodii.feature.apifoodii.meal.domain.usecase.SaveFoodiiMealUseCase
import com.example.foodii.feature.apifoodii.meal.presentation.screen.MealFoodiiDetailsUiState
import com.example.foodii.feature.mealdb.data.local.entity.PlannedMealEntity
import com.example.foodii.feature.mealdb.domain.entity.MealDetail
import com.example.foodii.feature.mealdb.domain.usecase.GetPlannedMealsUseCase
import com.example.foodii.feature.mealdb.domain.usecase.PlanMealUseCase
import com.example.foodii.feature.mealdb.presentation.widget.MealReminderWidget
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

class MealFoodiiViewModel(
    private val saveFoodiiMealUseCase: SaveFoodiiMealUseCase,
    private val getMealsByDateRangeUseCase: GetMealsByDateRangeUseCase,
    private val getMealsUseCase: GetMealsUseCase,
    private val getFoodiiMealByIdUseCase: GetFoodiiMealByIdUseCase,
    private val planMealUseCase: PlanMealUseCase,
    private val getPlannedMealsUseCase: GetPlannedMealsUseCase,
    private val context: Context,
    val shakeDetector: ShakeDetector
) : ViewModel() {

    private val _uiState = MutableStateFlow(MealFoodiiDetailsUiState())
    val uiState = _uiState.asStateFlow()

    private val _summaries = MutableStateFlow<List<DailySummary>>(emptyList())
    val summaries = _summaries.asStateFlow()

    private val _allMeals = MutableStateFlow<List<FoodiiMeal>>(emptyList())
    val allMeals = _allMeals.asStateFlow()

    private val _selectedMeal = MutableStateFlow<FoodiiMeal?>(null)
    val selectedMeal = _selectedMeal.asStateFlow()

    private val _plannedMeals = MutableStateFlow<List<PlannedMealEntity>>(emptyList())
    val plannedMeals = _plannedMeals.asStateFlow()

    private val _currentStep = MutableStateFlow(1)
    val currentStep = _currentStep.asStateFlow()

    fun startShakeDetection() {
        shakeDetector.startListening {
            if (_currentStep.value < 3) {
                _currentStep.value += 1
                Log.d("Shake", "Cambiando al paso ${_currentStep.value}")
            } else {
                _currentStep.value = 1
                Log.d("Shake", "Reiniciando pasos")
            }
        }
    }

    fun stopShakeDetection() {
        shakeDetector.stopListening()
    }

    override fun onCleared() {
        super.onCleared()
        shakeDetector.stopListening()
    }

    fun loadAllMeals(userId: String) {
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            try {
                getMealsUseCase(userId).collect { meals ->
                    _allMeals.value = meals
                    _uiState.update { it.copy(isLoading = false) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.localizedMessage) }
            }
        }
    }

    fun loadMealDetail(mealId: String, userId: String) {
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            val result = getFoodiiMealByIdUseCase(mealId, userId)
            _uiState.update { state ->
                result.fold(
                    onSuccess = { meal ->
                        _selectedMeal.value = meal
                        state.copy(isLoading = false)
                    },
                    onFailure = { error ->
                        state.copy(isLoading = false, error = error.message)
                    }
                )
            }
        }
    }

    fun loadMealsRange(userId: String, startDate: String, endDate: String) {
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            try {
                viewModelScope.launch {
                    getPlannedMealsUseCase().collect { localPlanned ->
                        _plannedMeals.value = localPlanned
                        updateCombinedSummaries(userId, startDate, endDate)
                    }
                }
                
                getMealsByDateRangeUseCase(userId, startDate, endDate).collect { apiSummaries ->
                    _summaries.value = apiSummaries
                    updateCombinedSummaries(userId, startDate, endDate)
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.localizedMessage) }
            }
        }
    }

    private fun updateCombinedSummaries(userId: String, startDate: String, endDate: String) {
        val apiSummaries = _summaries.value
        val localPlanned = _plannedMeals.value

        val localGrouped = localPlanned.groupBy { 
            Instant.ofEpochMilli(it.date).atZone(ZoneId.of("UTC")).toLocalDate().toString()
        }

        val combined = apiSummaries.map { summary ->
            val extraMeals = localGrouped[summary.date]?.map { 
                FoodiiMeal(
                    id = it.mealId,
                    name = it.name,
                    date = Instant.ofEpochMilli(it.date).atZone(ZoneId.of("UTC")).toLocalDate(),
                    mealTime = FoodiiMealTime.SNACK,
                    totalCalories = 0.0,
                    createdBy = userId,
                    ingredients = emptyList()
                )
            } ?: emptyList()
            
            summary.copy(meals = summary.meals + extraMeals)
        }
        
        val apiDates = apiSummaries.map { it.date }.toSet()
        val extraSummaries = localGrouped.filter { it.key !in apiDates }.map { (date, meals) ->
            DailySummary(
                date = date,
                totalCalories = 0.0,
                meals = meals.map { 
                    FoodiiMeal(
                        id = it.mealId,
                        name = it.name,
                        date = Instant.ofEpochMilli(it.date).atZone(ZoneId.of("UTC")).toLocalDate(),
                        mealTime = FoodiiMealTime.SNACK,
                        totalCalories = 0.0,
                        createdBy = userId,
                        ingredients = emptyList()
                    )
                }
            )
        }

        _summaries.value = (combined + extraSummaries).sortedBy { it.date }
        _uiState.update { it.copy(isLoading = false) }
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

    fun scheduleMealReminder(meal: FoodiiMeal, dateMillis: Long) {
        viewModelScope.launch {
            try {
                val mealDetail = MealDetail(
                    id = meal.id,
                    name = meal.name,
                    instructions = "Comida programada desde Foodii",
                    imageUrl = ""
                )
                planMealUseCase(mealDetail, dateMillis)
                MealReminderWidget().updateAll(context)
                Log.d("MealFoodiiViewModel", "Recordatorio de comida agendado correctamente")
            } catch (e: Exception) {
                Log.e("MealFoodiiViewModel", "Error al agendar recordatorio", e)
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
