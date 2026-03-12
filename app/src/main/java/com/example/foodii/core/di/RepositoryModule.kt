package com.example.foodii.core.di

import com.example.foodii.feature.apifoodii.ingredient.data.repositories.IngredientFoodiiRepositoryImpl
import com.example.foodii.feature.apifoodii.ingredient.domain.repository.IngredientRepository
import com.example.foodii.feature.apifoodii.meal.data.repositories.MealFoodiiRepositoryImpl
import com.example.foodii.feature.apifoodii.meal.domain.repository.MealFoodiiRepository
import com.example.foodii.feature.auth.data.repositories.AuthRepositoryImpl
import com.example.foodii.feature.auth.domain.repository.AuthRepository
import com.example.foodii.feature.mealdb.data.datasource.repositories.PlannerRepositoryImpl
import com.example.foodii.feature.mealdb.domain.repository.PlannerRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindMealFoodiiRepository(impl: MealFoodiiRepositoryImpl): MealFoodiiRepository

    @Binds
    @Singleton
    abstract fun bindIngredientRepository(impl: IngredientFoodiiRepositoryImpl): IngredientRepository

    @Binds
    @Singleton
    abstract fun bindPlannerRepository(impl: PlannerRepositoryImpl): PlannerRepository
}
