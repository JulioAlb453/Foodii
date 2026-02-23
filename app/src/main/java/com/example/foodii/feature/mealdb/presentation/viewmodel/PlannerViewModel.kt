package com.example.foodii.feature.mealdb.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodii.feature.mealdb.domain.repository.PlannerRepository
import com.example.foodii.feature.mealdb.data.local.entity.PlannedMealEntity
import com.example.foodii.feature.mealdb.domain.usecase.GetPlannedMealsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class PlannerViewModel @Inject constructor(
    private val getPlannedMealsUseCase: GetPlannedMealsUseCase
) : ViewModel() {

    val plannedMeals: StateFlow<List<PlannedMealEntity>> = getPlannedMealsUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}
