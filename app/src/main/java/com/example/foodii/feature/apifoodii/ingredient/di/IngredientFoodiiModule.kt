package com.example.foodii.feature.apifoodii.ingredient.di

import com.example.foodii.feature.apifoodii.ingredient.domain.repository.IngredientRepository
import com.example.foodii.feature.apifoodii.ingredient.domain.usecase.*
import com.example.foodii.feature.apifoodii.ingredient.presentation.viemodel.IngredientViewModelFactory
import com.example.foodii.feature.auth.domain.repository.AuthRepository

class IngredientFoodiiModule(
    private val ingredientRepository: IngredientRepository,
    private val authRepository: AuthRepository
) {

    private fun provideGetIngredientsUseCase(): GetIngredientsUseCase {
        return GetIngredientsUseCase(ingredientRepository)
    }

    private fun provideCreateIngredientUseCase(): CreateIngredientUseCase {
        return CreateIngredientUseCase(ingredientRepository)
    }

    private fun provideUpdateIngredientUseCase(): UpdateIngredientUseCase {
        return UpdateIngredientUseCase(ingredientRepository)
    }

    private fun provideDeleteIngredientUseCase(): DeleteIngredientUseCase {
        return DeleteIngredientUseCase(ingredientRepository)
    }

    private fun provideCalculateCaloriesUseCase(): CalculateCaloriesUseCase {
        return CalculateCaloriesUseCase(ingredientRepository)
     }

    fun provideIngredientViewModelFactory(): IngredientViewModelFactory {
        return IngredientViewModelFactory(
            getIngredientsUseCase = provideGetIngredientsUseCase(),
            createIngredientUseCase = provideCreateIngredientUseCase(),
            updateIngredientUseCase = provideUpdateIngredientUseCase(),
            deleteIngredientUseCase = provideDeleteIngredientUseCase(),
            calculateCaloriesUseCase = provideCalculateCaloriesUseCase(),
            authRepository = authRepository
        )
    }
}
