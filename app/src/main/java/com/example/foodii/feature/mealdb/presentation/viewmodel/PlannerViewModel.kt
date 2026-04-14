package com.example.foodii.feature.mealdb.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodii.feature.auth.data.datasource.local.AuthLocalDataSource
import com.example.foodii.feature.mealdb.domain.repository.PlannerRepository
import com.example.foodii.feature.mealdb.data.local.entity.PlannedMealEntity
import com.example.foodii.feature.mealdb.domain.usecase.GetPlannedMealsUseCase
import com.example.foodii.feature.mealdb.domain.usecase.UpdatePlannedMealDateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlannerViewModel @Inject constructor(
    private val getPlannedMealsUseCase: GetPlannedMealsUseCase,
    private val updatePlannedMealDateUseCase: UpdatePlannedMealDateUseCase,
    private val authLocalDataSource: AuthLocalDataSource
) : ViewModel() {

    @OptIn(ExperimentalCoroutinesApi::class)
    val plannedMeals: StateFlow<List<PlannedMealEntity>> = authLocalDataSource.getUser()
        .flatMapLatest { user ->
            if (user != null) {
                getPlannedMealsUseCase(user.id)
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun updateMealDate(plannedMealId: Int, newDate: Long) {
        viewModelScope.launch {
            val user = authLocalDataSource.getUser().firstOrNull()
            if (user != null) {
                updatePlannedMealDateUseCase(plannedMealId, newDate, user.id)
            }
        }
    }
}
