package com.example.foodii.feature.foods.data.datasource.remote.mapper

import com.example.foodii.feature.foods.data.datasource.remote.model.MealDTO
import com.example.foodii.feature.foods.domain.entity.MealDetail

fun MealDTO.toMealDetail(): MealDetail{
    return MealDetail(
        id = idMeal ?: "",
        name = strMeal ?: "Sin nombre",
        instructions = strInstructions ?: "Sin instrucciones",
        imageUrl = strMealThumb ?: ""
    )
}