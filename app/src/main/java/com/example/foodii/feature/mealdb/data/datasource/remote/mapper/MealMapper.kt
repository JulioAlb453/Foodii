package com.example.foodii.feature.mealdb.data.datasource.remote.mapper

import com.example.foodii.feature.mealdb.data.datasource.remote.model.MealDbDto
import com.example.foodii.feature.mealdb.domain.entity.MealDetail


fun MealDbDto.toDomain(): MealDetail {
    return MealDetail(
        id = idMeal ?: "",
        name = strMeal ?: "",
        instructions = strInstructions ?: "",
        imageUrl = strMealThumb ?: ""
    )

}