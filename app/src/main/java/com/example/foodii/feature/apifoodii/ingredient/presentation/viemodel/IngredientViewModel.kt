package com.example.foodii.feature.apifoodii.ingredient.presentation.viemodel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodii.feature.apifoodii.ingredient.domain.usecase.CalculateCaloriesUseCase
import com.example.foodii.feature.apifoodii.ingredient.domain.usecase.GetIngredientsUseCase
import com.example.foodii.feature.apifoodii.ingredient.presentation.screen.IngredientFoodiiDetailsUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class IngredientViewModel(
    private val getIngredientsUseCase: GetIngredientsUseCase,
    private val calculateCaloriesUseCase: CalculateCaloriesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(IngredientFoodiiDetailsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadIngredients()
    }

    private fun loadIngredients() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            val result = getIngredientsUseCase(userId = "")
            _uiState.update { state ->
                result.fold(
                    onSuccess = { list -> state.copy(isLoading = false, ingredients = list) },
                    onFailure = { err -> state.copy(isLoading = false, error = err.message) }
                )
            }
        }
    }

    fun calculate(ingredientId: String, amount: Int, userId: String) {
        viewModelScope.launch {
            val result = calculateCaloriesUseCase(ingredientId, amount, userId)
            _uiState.update { state ->
                result.fold(
                    onSuccess = { res -> state.copy(calculation = res, error = null) },
                    onFailure = { err -> state.copy(error = err.message) }
                )
            }
        }
    }
}