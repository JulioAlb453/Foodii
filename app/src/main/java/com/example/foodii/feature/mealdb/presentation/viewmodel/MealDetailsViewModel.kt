package com.example.foodii.feature.mealdb.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodii.feature.auth.data.datasource.local.AuthLocalDataSource
import com.example.foodii.feature.mealdb.presentation.screen.MealDetailsUIState
import com.example.foodii.feature.mealdb.domain.usecase.GetMealInstructionsUseCase
import com.example.foodii.feature.mealdb.domain.entity.MealDetail
import com.example.foodii.feature.mealdb.domain.usecase.PlanMealUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MealDetailsViewModel @Inject constructor(
    private val getMealInstructionsUseCase: GetMealInstructionsUseCase,
    private val planMealUseCase: PlanMealUseCase,
    private val authLocalDataSource: AuthLocalDataSource
) : ViewModel() {

    private val _uiState = MutableStateFlow(MealDetailsUIState())
    val uiState = _uiState.asStateFlow()

    init {
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
            val user = authLocalDataSource.getUser().firstOrNull()
            val userId = user?.id ?: ""
            planMealUseCase(meal, dateMillis, userId)
        }
    }
}
