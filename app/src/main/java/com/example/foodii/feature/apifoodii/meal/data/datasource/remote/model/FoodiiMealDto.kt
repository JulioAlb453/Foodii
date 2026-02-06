package com.example.foodii.feature.apifoodii.meal.data.datasource.remote.model

data class FoodiiMealResponse(
    val meals: List<FoodiiMealDto>
)

data class FoodiiMealDto(
    val id: String?,
    val name: String?,
    val date: String?,
    val meal_time: String?,
    val total_calories: Int?,
    val created_by: String?,
    val ingredients: List<FoodiiMealIngredientDto>?
)

data class FoodiiMealIngredientDto(
    val ingredient_id: String?,
    val name: String?,
    val amount: Int?,
    val calories: Int?
)
