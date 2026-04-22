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
import com.example.foodii.feature.apifoodii.meal.domain.entity.DailySummary
import com.example.foodii.feature.apifoodii.meal.domain.entity.FoodiiMeal
import com.example.foodii.feature.apifoodii.meal.domain.entity.FoodiiMealTime
import com.example.foodii.feature.apifoodii.meal.domain.usecase.GetFoodiiMealByIdUseCase
import com.example.foodii.feature.apifoodii.meal.domain.usecase.GetMealsByDateRangeUseCase
import com.example.foodii.feature.apifoodii.meal.domain.usecase.GetMealsUseCase
import com.example.foodii.feature.apifoodii.ingredient.domain.entity.Ingredient
import com.example.foodii.feature.apifoodii.ingredient.domain.repository.IngredientRepository
import com.example.foodii.feature.apifoodii.meal.domain.repository.ImageRepository
import com.example.foodii.feature.apifoodii.meal.domain.usecase.SaveFoodiiMealUseCase
import com.example.foodii.feature.apifoodii.meal.presentation.screen.MealFoodiiDetailsUiState
import com.example.foodii.feature.mealdb.data.local.entity.PlannedMealEntity
import com.example.foodii.feature.mealdb.domain.entity.MealDetail
import com.example.foodii.feature.mealdb.domain.usecase.GetPlannedMealsUseCase
import com.example.foodii.feature.mealdb.domain.usecase.PlanMealUseCase
import com.example.foodii.feature.mealdb.presentation.widget.MealReminderWidget
import com.example.foodii.feature.mealdb.data.worker.SingleMealNotificationWorker
import com.example.foodii.feature.mealdb.domain.usecase.UpdatePlannedMealDateUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.concurrent.TimeUnit
import java.util.concurrent.CancellationException

class MealFoodiiViewModel(
    private val saveFoodiiMealUseCase: SaveFoodiiMealUseCase,
    private val getMealsByDateRangeUseCase: GetMealsByDateRangeUseCase,
    private val getMealsUseCase: GetMealsUseCase,
    private val getFoodiiMealByIdUseCase: GetFoodiiMealByIdUseCase,
    private val planMealUseCase: PlanMealUseCase,
    private val getPlannedMealsUseCase: GetPlannedMealsUseCase,
    private val updatePlannedMealDateUseCase: UpdatePlannedMealDateUseCase,
    private val ingredientRepository: IngredientRepository,
    private val context: Context,
    val shakeDetector: ShakeDetector,
    private val cameraManager: CameraManager,
    private val imageRepository: ImageRepository
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

    private val _ingredientsForMealForm = MutableStateFlow<List<Ingredient>>(emptyList())
    val ingredientsForMealForm = _ingredientsForMealForm.asStateFlow()

    private val _currentStep = MutableStateFlow(1)
    val currentStep = _currentStep.asStateFlow()

    private val _selectedIngredients = MutableStateFlow<List<Pair<Ingredient, Int>>>(emptyList())
    val selectedIngredients = _selectedIngredients.asStateFlow()

    private val _selectedCategories = MutableStateFlow<Set<String>>(emptySet())
    val selectedCategories = _selectedCategories.asStateFlow()

    private val _capturedImageUri = MutableStateFlow<Uri?>(null)
    val capturedImageUri = _capturedImageUri.asStateFlow()

    private var rangeLoadJob: Job? = null

    fun startShakeDetection() {
        shakeDetector.startListening {
            if (_currentStep.value < 3) {
                _currentStep.value += 1
            } else {
                _currentStep.value = 1
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

    fun loadIngredientsForMealForm(userId: String) {
        viewModelScope.launch {
            _ingredientsForMealForm.value = try {
                ingredientRepository.getAllIngredients(userId)
            } catch (e: Exception) {
                emptyList()
            }
        }
    }

    fun clearSuccessData() {
        _uiState.update { it.copy(successData = null) }
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
                if (e !is CancellationException) {
                    _uiState.update { it.copy(isLoading = false, error = e.localizedMessage) }
                }
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
        rangeLoadJob?.cancel()
        
        _uiState.update { it.copy(isLoading = true, error = null) }
        rangeLoadJob = viewModelScope.launch {
            try {
                launch {
                    getMealsUseCase(userId).collect { meals ->
                        _allMeals.value = meals
                        updateCombinedSummaries(userId, startDate, endDate)
                    }
                }

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
                if (e !is CancellationException) {
                    _uiState.update { it.copy(isLoading = false, error = e.localizedMessage) }
                }
            }
        }
    }

    private fun updateCombinedSummaries(userId: String, startDate: String, endDate: String) {
        val apiSummaries = _summaries.value
        val localPlanned = _plannedMeals.value
        val allCreatedMeals = _allMeals.value

        val localGrouped = localPlanned.groupBy { 
            val date = Instant.ofEpochMilli(it.date).atZone(ZoneOffset.UTC).toLocalDate()
            date.toString()
        }

        val combined = apiSummaries.map { summary ->
            val summaryDate = summary.date.substringBefore("T")
            val localMealsMap = localGrouped[summaryDate]?.associateBy { it.mealId } ?: emptyMap()
            
            val updatedApiMeals = summary.meals.map { apiMeal ->
                apiMeal.copy(plannedId = localMealsMap[apiMeal.id]?.id)
            }
            
            val apiMealIds = updatedApiMeals.map { it.id }.toSet()
            
            val extraMeals = (localGrouped[summaryDate] ?: emptyList())
                .filter { it.mealId !in apiMealIds }
                .map { planned ->
                    val matchingMeal = allCreatedMeals.find { it.id == planned.mealId }
                    FoodiiMeal(
                        id = planned.mealId,
                        name = planned.name,
                        date = Instant.ofEpochMilli(planned.date).atZone(ZoneOffset.UTC).toLocalDate(),
                        mealTime = FoodiiMealTime.SNACK,
                        totalCalories = matchingMeal?.totalCalories ?: 0.0,
                        createdBy = userId,
                        steps = emptyList(),
                        image = matchingMeal?.image,
                        ingredients = emptyList(),
                        plannedId = planned.id
                    )
                }
            
            val allDayMeals = (updatedApiMeals + extraMeals).distinctBy { it.id }
            summary.copy(
                date = summaryDate, 
                meals = allDayMeals,
                totalCalories = allDayMeals.sumOf { it.totalCalories }
            )
        }
        
        val apiDates = combined.map { it.date }.toSet()
        val extraSummaries = localGrouped.filter { it.key !in apiDates }.map { (date, meals) ->
            val mappedMeals = meals.distinctBy { it.mealId }.map { planned ->
                val matchingMeal = allCreatedMeals.find { it.id == planned.mealId }
                FoodiiMeal(
                    id = planned.mealId,
                    name = planned.name,
                    date = Instant.ofEpochMilli(planned.date).atZone(ZoneOffset.UTC).toLocalDate(),
                    mealTime = FoodiiMealTime.SNACK,
                    totalCalories = matchingMeal?.totalCalories ?: 0.0,
                    createdBy = userId,
                    steps = emptyList(),
                    image = matchingMeal?.image,
                    ingredients = emptyList(),
                    plannedId = planned.id
                )
            }
            DailySummary(
                date = date,
                totalCalories = mappedMeals.sumOf { it.totalCalories },
                meals = mappedMeals
            )
        }

        val finalResult = (combined + extraSummaries).sortedBy { it.date }
        _summaries.value = finalResult
        _uiState.update { it.copy(isLoading = false) }
    }

    fun onTakePhoto(onUriReady: (Uri?) -> Unit) {
        cameraManager.capturePhoto { uri ->
            _capturedImageUri.value = uri
            onUriReady(uri)
        }
    }

    fun addIngredientToDraft(ingredient: Ingredient, amount: Int) {
        val currentList = _selectedIngredients.value.toMutableList()
        currentList.add(ingredient to amount)
        _selectedIngredients.value = currentList
    }

    fun removeIngredientFromDraft(ingredientId: String) {
        val currentList = _selectedIngredients.value.filter { it.first.id != ingredientId }
        _selectedIngredients.value = currentList
    }

    fun onCategoryToggled(slug: String) {
        val current = _selectedCategories.value
        val updated = if (current.contains(slug)) current - slug else current + slug
        _selectedCategories.value = updated
    }

    fun saveMeal(
        name: String,
        date: LocalDate,
        mealTime: FoodiiMealTime,
        ingredients: List<Pair<String, Int>>,
        steps: List<String>,
        userId: String,
        imageUri: Uri? = null,
        categories: List<String> = emptyList()
    ) {
        if (userId.isBlank()) {
            _uiState.update { it.copy(error = "No se pudo identificar al usuario.") }
            return
        }

        _uiState.update { it.copy(isLoading = true, error = null, successData = null) }
        
        viewModelScope.launch {
            try {
                var imageUrl: String? = null
                if (imageUri != null) {
                    val uploadResult = imageRepository.uploadImage(imageUri)
                    imageUrl = uploadResult.getOrNull()
                }

                val result = saveFoodiiMealUseCase(
                    name = name,
                    date = date,
                    mealTime = mealTime,
                    ingredientsRequest = ingredients,
                    steps = steps,
                    userId = userId,
                    image = imageUrl,
                    categories = categories
                )

                _uiState.update { currentState ->
                    result.fold(
                        onSuccess = { meal ->
                            currentState.copy(isLoading = false, successData = meal)
                        },
                        onFailure = { error ->
                            currentState.copy(isLoading = false, error = error.message ?: "Error desconocido")
                        },
                    )
                }
            } catch (e: Exception) {
                if (e !is CancellationException) {
                    _uiState.update { it.copy(isLoading = false, error = e.localizedMessage) }
                }
            }
        }
    }

    fun scheduleMealReminder(meal: FoodiiMeal, dateMillis: Long) {
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            try {
                if (meal.plannedId != null) {
                    updatePlannedMealDateUseCase(meal.plannedId, dateMillis, meal.createdBy)
                } else {
                    val mealDetail = MealDetail(
                        id = meal.id,
                        name = meal.name,
                        instructions = meal.stepsPlainText().ifEmpty { "Toca para ver la receta" },
                        imageUrl = meal.image ?: "",
                    )
                    planMealUseCase(mealDetail, dateMillis, meal.createdBy)
                }
                
                val currentTime = System.currentTimeMillis()
                val delay = dateMillis - currentTime
                
                if (delay > 0) {
                    WorkManager.getInstance(context).cancelAllWorkByTag("alert_${meal.id}")

                    val workRequest = OneTimeWorkRequestBuilder<SingleMealNotificationWorker>()
                        .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                        .setInputData(workDataOf(
                            "meal_name" to meal.name,
                            "meal_id" to meal.id
                        ))
                        .addTag("alert_${meal.id}")
                        .build()

                    WorkManager.getInstance(context).enqueue(workRequest)
                }

                MealReminderWidget().updateAll(context)
                
                // Forzar recarga inmediata local
                val start = LocalDate.now().minusMonths(1).toString()
                val end = LocalDate.now().plusYears(1).toString()
                loadMealsRange(meal.createdBy, start, end)
                
            } catch (e: Exception) {
                if (e !is CancellationException) {
                    _uiState.update { it.copy(isLoading = false, error = e.localizedMessage) }
                }
            }
        }
    }

    fun sendTestNotification(userId: String) {
        viewModelScope.launch {
            try {
                val nextMeal = getPlannedMealsUseCase.getNextPlannedMeal(userId)
                
                if (nextMeal != null) {
                    NotificationHelper.showMealAlert(
                        context = context,
                        title = "Próxima comida agendada",
                        message = "Tienes pendiente: ${nextMeal.name}",
                        mealId = nextMeal.mealId
                    )
                } else {
                    NotificationHelper.showMealAlert(
                        context = context,
                        title = "Sin agenda próxima",
                        message = "No tienes comidas programadas.",
                        mealId = "no_meals"
                    )
                }
            } catch (e: Exception) {
                if (e !is CancellationException) {
                    Log.e("MealFoodiiViewModel", "Error al enviar notificación de prueba", e)
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun loadRandomMeal(userId: String) {
    }
}
