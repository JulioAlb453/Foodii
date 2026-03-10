package com.example.foodii.feature.apifoodii.ingredient.presentation.viemodel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodii.feature.apifoodii.ingredient.domain.usecase.CalculateCaloriesUseCase
import com.example.foodii.feature.apifoodii.ingredient.domain.usecase.GetIngredientsUseCase
import com.example.foodii.feature.apifoodii.ingredient.presentation.screen.IngredientFoodiiDetailsUiState
import com.example.foodii.feature.auth.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class IngredientViewModel @Inject constructor(
    private val getIngredientsUseCase: GetIngredientsUseCase,
    private val calculateCaloriesUseCase: CalculateCaloriesUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(IngredientFoodiiDetailsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadIngredients()
    }

    private fun loadIngredients() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            val user = authRepository.getCurrentUser()
            if (user == null) {
                _uiState.update { it.copy(isLoading = false, error = "User not authenticated") }
                return@launch
            }

            // Cambiamos de token a userId para coincidir con el UseCase actualizado
            val result = getIngredientsUseCase(userId = user.id)
            _uiState.update { state ->
                result.fold(
                    onSuccess = { list -> state.copy(isLoading = false, ingredients = list) },
                    onFailure = { err -> state.copy(isLoading = false, error = err.message) }
                )
            }
        }
    }

    fun calculate(ingredientId: String, amount: Int) {
        viewModelScope.launch {
            val user = authRepository.getCurrentUser()
            if (user == null) {
                _uiState.update { it.copy(error = "User not authenticated") }
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
}
