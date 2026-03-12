package com.example.foodii.feature.apifoodii.ingredient.presentation.viemodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodii.feature.apifoodii.ingredient.domain.entity.Ingredient
import com.example.foodii.feature.apifoodii.ingredient.domain.usecase.*
import com.example.foodii.feature.apifoodii.ingredient.presentation.screen.IngredientFoodiiDetailsUiState
import com.example.foodii.feature.auth.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

class IngredientViewModel @Inject constructor(
    private val getIngredientsUseCase: GetIngredientsUseCase,
    private val createIngredientUseCase: CreateIngredientUseCase,
    private val updateIngredientUseCase: UpdateIngredientUseCase,
    private val deleteIngredientUseCase: DeleteIngredientUseCase,
    private val calculateCaloriesUseCase: CalculateCaloriesUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(IngredientFoodiiDetailsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadIngredients()
    }

    fun loadIngredients() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            val user = authRepository.getCurrentUser()
            if (user == null) {
                _uiState.update { it.copy(isLoading = false, error = "Usuario no autenticado") }
                return@launch
            }

            val result = getIngredientsUseCase(userId = user.id)
            _uiState.update { state ->
                result.fold(
                    onSuccess = { list -> state.copy(isLoading = false, ingredients = list, error = null) },
                    onFailure = { err -> state.copy(isLoading = false, error = err.message) }
                )
            }
        }
    }

    fun createIngredient(name: String, caloriesPer100g: Double) {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            val user = authRepository.getCurrentUser() ?: return@launch
            val newIngredient = Ingredient(
                id = UUID.randomUUID().toString(),
                name = name,
                caloriesPer100g = caloriesPer100g,
                createdBy = user.id,
                createdAt = java.util.Date()
            )

            val result = createIngredientUseCase(newIngredient, user.id)
            result.fold(
                onSuccess = {
                    loadIngredients()
                },
                onFailure = { err ->
                    _uiState.update { it.copy(isLoading = false, error = err.message) }
                }
            )
        }
    }

    fun updateIngredient(ingredient: Ingredient) {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            val user = authRepository.getCurrentUser() ?: return@launch
            val result = updateIngredientUseCase(ingredient, user.id)
            result.fold(
                onSuccess = {
                    loadIngredients()
                },
                onFailure = { err ->
                    _uiState.update { it.copy(isLoading = false, error = err.message) }
                }
            )
        }
    }

    fun deleteIngredient(id: String) {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            val user = authRepository.getCurrentUser() ?: return@launch
            val result = deleteIngredientUseCase(id, user.id)
            result.fold(
                onSuccess = {
                    loadIngredients()
                },
                onFailure = { err ->
                    _uiState.update { it.copy(isLoading = false, error = err.message) }
                }
            )
        }
    }

    fun calculate(ingredientId: String, amount: Int) {
        viewModelScope.launch {
            val user = authRepository.getCurrentUser()
            if (user == null) {
                _uiState.update { it.copy(error = "Usuario no autenticado") }
                return@launch
            }

            val result = calculateCaloriesUseCase(
                ingredientId = ingredientId,
                amount = amount,
                userId = user.id,
            )
            _uiState.update { state ->
                result.fold(
                    onSuccess = { res -> state.copy(calculation = res, error = null) },
                    onFailure = { err -> state.copy(error = err.message) }
                )
            }
        }
    }
    
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
