package com.example.foodii.feature.apifoodii.meal.data.repositories

import com.example.foodii.core.network.FoodiiAPI
import com.example.foodii.feature.apifoodii.meal.data.datasource.remote.mapper.toDomain
import com.example.foodii.feature.apifoodii.meal.domain.entity.FoodiiMeal
import com.example.foodii.feature.apifoodii.meal.domain.repository.FoodiiRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FoodiiRepositoryImpl(
    private val api: FoodiiAPI
) : FoodiiRepository {

    override fun getMeals(): Flow<List<FoodiiMeal>> = flow {
        val response = api.getMealsAPI()
        emit(response.meals.map { it.toDomain() })
    }

    override suspend fun saveMeal(meal: FoodiiMeal) {

    }

    override suspend fun getMealById(id: String): FoodiiMeal? {
        return null
    }

    override suspend fun deleteMeal(id: String) {

    }
}
