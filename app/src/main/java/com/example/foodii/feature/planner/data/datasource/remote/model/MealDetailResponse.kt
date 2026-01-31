package com.example.foodii.feature.planner.data.datasource.remote.model

data class MealDetailResponse(
    val meals: List<MealDTO>
)

data class MealDTO(
    val idMeal: String?,
    val strMeal: String?,
    val strInstructions: String?,
    val strMealThumb: String?
)