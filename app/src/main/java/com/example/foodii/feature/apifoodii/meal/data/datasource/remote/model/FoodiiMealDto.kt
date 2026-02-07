package com.example.foodii.feature.apifoodii.meal.data.datasource.remote.model

import com.google.gson.annotations.SerializedName

data class FoodiiMealResponse(
    @SerializedName("success") val success: Boolean?,
    @SerializedName("data") val meals: List<FoodiiMealDto>?
)

data class SingleMealResponse(
    @SerializedName("success") val success: Boolean?,
    @SerializedName("data") val meal: FoodiiMealDto?
)

data class FoodiiMealDto(
    @SerializedName("id") val id: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("date") val date: String?,
    @SerializedName("mealTime") val mealTime: String?,
    @SerializedName("ingredients") val ingredients: List<FoodiiMealIngredientDto>?,
    @SerializedName("totalCalories") val totalCalories: Double?,
    @SerializedName("CreatedBy") val createdBy: String?,
    @SerializedName("createdAt") val createdAt: String?
)

data class FoodiiMealIngredientDto(
    @SerializedName("ingredientId") val ingredientId: String?,
    @SerializedName("amount") val amount: Int?,
    @SerializedName("ingredientName") val ingredientName: String?,
    @SerializedName("calories") val calories: Double?
)
