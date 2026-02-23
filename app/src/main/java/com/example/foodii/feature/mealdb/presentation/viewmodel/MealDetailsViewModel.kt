package com.example.foodii.feature.mealdb.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodii.feature.mealdb.presentation.screen.MealDetailsUIState
import com.example.foodii.feature.mealdb.domain.usecase.GetMealInstructionsUseCase
import com.example.foodii.feature.mealdb.domain.entity.MealDetail
import com.example.foodii.feature.mealdb.domain.usecase.PlanMealUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MealDetailsViewModel @Inject constructor(
    private val getMealInstructionsUseCase: GetMealInstructionsUseCase,
    private val planMealUseCase: PlanMealUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MealDetailsUIState())
    val uiState = _uiState.asStateFlow()

    init {
        // Por defecto cargamos con la letra 'a' o podrías recibirlo de un SavedStateHandle
        loadMealDetails("a")
    }

    fun loadMealDetails(letter: String) {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            val result = getMealInstructionsUseCase(letter)
            _uiState.update { currentState ->
                result.fold(
                    onSuccess = { list ->
                        currentState.copy(isLoading = false, meals = list, error = null)
                    },
                    onFailure = { error ->
                        currentState.copy(isLoading = false, error = error.message)
                    }
                )
            }
        }
    }

    fun onPlanMealSelected(meal: MealDetail, dateMillis: Long) {
        viewModelScope.launch {
            planMealUseCase(meal, dateMillis)
        }
    }
}
