package com.example.foodii.feature.apifoodii.data.datasource.remote.model

import com.google.gson.annotations.SerializedName


data class FoodiiMealResponse(
    @SerializedName("meals") val meals: List<FoodiiMealDto>
)

data class FoodiiMealDto(
    @SerializedName("id") val id: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("date") val date: String?,
    @SerializedName("meal_time") val mealTime: String?,
    @SerializedName("total_calories") val totalCalories: Double?,
    @SerializedName("created_by") val createdBy: String?,
    @SerializedName("ingredients") val ingredients: List<FoodiiMealIngredientDto>? = null
)

data class FoodiiMealIngredientDto(
    @SerializedName("ingredient_id") val ingredientId: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("amount") val amount: Double?
)
