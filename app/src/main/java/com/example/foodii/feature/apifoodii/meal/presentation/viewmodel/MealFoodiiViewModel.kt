package com.example.foodii.feature.apifoodii.meal.presentation.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.glance.appwidget.updateAll
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.foodii.core.hardware.domain.CameraManager
import com.example.foodii.core.hardware.domain.ShakeDetector
import com.example.foodii.core.utils.NotificationHelper
import com.example.foodii.feature.apifoodii.ingredient.domain.entity.Ingredient
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
import com.example.foodii.feature.mealdb.data.worker.SingleMealNotificationWorker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.concurrent.TimeUnit

class MealFoodiiViewModel(
    private val saveFoodiiMealUseCase: SaveFoodiiMealUseCase,
    private val getMealsByDateRangeUseCase: GetMealsByDateRangeUseCase,
    private val getMealsUseCase: GetMealsUseCase,
    private val getFoodiiMealByIdUseCase: GetFoodiiMealByIdUseCase,
    private val planMealUseCase: PlanMealUseCase,
    private val getPlannedMealsUseCase: GetPlannedMealsUseCase,
    private val context: Context,
    val shakeDetector: ShakeDetector,
    val cameraManager: CameraManager
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

    private val _selectedIngredients = MutableStateFlow<List<Pair<Ingredient, Int>>>(emptyList())
    val selectedIngredients = _selectedIngredients.asStateFlow()

    private val _capturedImageUri = MutableStateFlow<Uri?>(null)
    val capturedImageUri = _capturedImageUri.asStateFlow()

    fun addIngredientToDraft(ingredient: Ingredient, amount: Int) {
        _selectedIngredients.update { it + (ingredient to amount) }
    }

    fun removeIngredientFromDraft(ingredientId: String) {
        _selectedIngredients.update { it.filter { it.first.id != ingredientId } }
    }

    fun onTakePhoto(onUriReady: (Uri?) -> Unit) {
        cameraManager.capturePhoto { uri ->
            _capturedImageUri.value = uri
            onUriReady(uri)
        }
    }

    fun clearForm() {
        _selectedIngredients.value = emptyList()
        _capturedImageUri.value = null
    }

    fun loadAllMeals(userId: String) {
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            try {
                getMealsUseCase(userId).collect { meals ->
                    _allMeals.value = meals
                    updateCombinedSummaries(userId, "", "")
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

    fun saveMeal(
        name: String,
        date: LocalDate,
        mealTime: FoodiiMealTime,
        ingredients: List<Pair<String, Int>>,
        userId: String,
        imageUri: Uri? = null
    ) {
        _uiState.update { it.copy(isLoading = true, error = null, successData = null) }
        viewModelScope.launch {
            try {
                val imagePath = imageUri?.toString()
                val result = saveFoodiiMealUseCase(name, date, mealTime, ingredients, userId, imagePath)
                result.fold(
                    onSuccess = { meal ->
                        loadAllMeals(userId)
                        clearForm()
                        _uiState.update { it.copy(isLoading = false, successData = meal) }
                    },
                    onFailure = { error ->
                        _uiState.update { it.copy(isLoading = false, error = error.message ?: "Error") }
                    }
                )
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.localizedMessage) }
            }
        }
    }

    fun loadMealsRange(userId: String, startDate: String, endDate: String) {
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            try {
                launch {
                    getPlannedMealsUseCase(userId).collect { localPlanned ->
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
        val allCreatedMeals = _allMeals.value
        val localGrouped = localPlanned.groupBy { 
            Instant.ofEpochMilli(it.date).atZone(ZoneId.of("UTC")).toLocalDate().toString()
        }

        val combined = apiSummaries.map { summary ->
            val summaryDate = summary.date.substringBefore("T")
            val extraMeals = localGrouped[summaryDate]?.map { planned ->
                val matchingMeal = allCreatedMeals.find { it.id == planned.mealId }
                FoodiiMeal(
                    id = planned.mealId,
                    name = planned.name,
                    date = Instant.ofEpochMilli(planned.date).atZone(ZoneId.of("UTC")).toLocalDate(),
                    mealTime = FoodiiMealTime.SNACK,
                    totalCalories = matchingMeal?.totalCalories ?: 0.0,
                    createdBy = userId,
                    ingredients = emptyList(),
                    image = matchingMeal?.image ?: planned.imageUrl
                )
            } ?: emptyList()
            summary.copy(meals = (summary.meals + extraMeals).distinctBy { it.id })
        }

        val apiDates = combined.map { it.date }.toSet()
        val extraSummaries = localGrouped.filter { it.key !in apiDates }.map { (date, meals) ->
            val mappedMeals = meals.map { planned ->
                val matchingMeal = allCreatedMeals.find { it.id == planned.mealId }
                FoodiiMeal(
                    id = planned.mealId,
                    name = planned.name,
                    date = Instant.ofEpochMilli(planned.date).atZone(ZoneId.of("UTC")).toLocalDate(),
                    mealTime = FoodiiMealTime.SNACK,
                    totalCalories = matchingMeal?.totalCalories ?: 0.0,
                    createdBy = userId,
                    ingredients = emptyList(),
                    image = matchingMeal?.image ?: planned.imageUrl
                )
            }
            DailySummary(date = date, totalCalories = mappedMeals.sumOf { it.totalCalories }, meals = mappedMeals)
        }

        _summaries.value = (combined + extraSummaries).sortedBy { it.date }
        _uiState.update { it.copy(isLoading = false) }
    }

    fun scheduleMealReminder(meal: FoodiiMeal, dateMillis: Long) {
        viewModelScope.launch {
            try {
                val mealDetail = MealDetail(id = meal.id, name = meal.name, instructions = "Toca para ver receta", imageUrl = meal.image ?: "")
                planMealUseCase(mealDetail, dateMillis, meal.createdBy)

                loadMealsRange(meal.createdBy, "2023-01-01", "2025-12-31")

                val delay = dateMillis - System.currentTimeMillis()
                if (delay > 0) {
                    val workRequest = OneTimeWorkRequestBuilder<SingleMealNotificationWorker>()
                        .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                        .setInputData(workDataOf("meal_name" to meal.name, "meal_id" to meal.id))
                        .build()
                    WorkManager.getInstance(context).enqueue(workRequest)
                }
                MealReminderWidget().updateAll(context)
            } catch (e: Exception) {
                Log.e("MealFoodiiViewModel", "Error al agendar: ${e.message}")
            }
        }
    }

    fun loadRandomMeal(userId: String) {
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            try {
                val meals = _allMeals.value.filter { it.createdBy == userId }
                if (meals.isEmpty()) {
                    _uiState.update { it.copy(isLoading = false, error = "No tienes comidas registradas") }
                } else {
                    val random = meals.random()
                    _uiState.update { it.copy(isLoading = false, randomMeal = random) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.localizedMessage) }
            }
        }
    }

    fun sendTestNotification(userId: String) {
        viewModelScope.launch {
            try {
                val nextMeal = getPlannedMealsUseCase.getNextPlannedMeal(userId)
                if (nextMeal != null) {
                    NotificationHelper.showMealAlert(context, "Próxima comida", "Pendiente: ${nextMeal.name}", nextMeal.mealId)
                } else {
                    NotificationHelper.showMealAlert(context, "Sin agenda", "No hay comidas programadas.", "no_meals")
                }
            } catch (e: Exception) {
                Log.e("MealFoodiiViewModel", "Error: ${e.message}")
            }
        }
    }

    fun clearError() { _uiState.update { it.copy(error = null) } }
    fun stopShakeDetection() { shakeDetector.stopListening() }
    override fun onCleared() { super.onCleared(); stopShakeDetection() }
}
