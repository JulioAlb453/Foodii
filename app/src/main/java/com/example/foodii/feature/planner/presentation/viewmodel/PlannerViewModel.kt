package com.example.foodii.feature.planner.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodii.feature.planner.domain.repository.PlannerRepository
import com.example.foodii.feature.planner.data.local.entity.PlannedMealEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class PlannerViewModel(
    private val repository: PlannerRepository
) : ViewModel() {

    val plannedMeals: StateFlow<List<PlannedMealEntity>> = repository.getPlannedMeals()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}