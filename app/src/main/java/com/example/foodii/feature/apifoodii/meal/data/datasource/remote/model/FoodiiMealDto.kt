package com.example.foodii.feature.apifoodii.meal.data.datasource.remote.model

import com.google.gson.annotations.SerializedName

data class FoodiiMealResponse(
    @SerializedName("success") val success: Boolean?,
    @SerializedName("data") val meals: List<FoodiiMealDto>?,
)

data class SingleMealResponse(
    @SerializedName("success") val success: Boolean?,
    @SerializedName("data") val meal: FoodiiMealDto?,
)

data class MealStepDto(
    @SerializedName("stepOrder") val stepOrder: Int?,
    @SerializedName("description") val description: String?,
)

data class FoodiiMealDto(
    @SerializedName("id") val id: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("date") val date: String?,
    @SerializedName("mealTime") val mealTime: String?,
    @SerializedName("ingredients") val ingredients: List<FoodiiMealIngredientDto>?,
    @SerializedName("totalCalories") val totalCalories: Double?,
    @SerializedName("steps") val steps: List<MealStepDto>?,
    @SerializedName("image") val image: String?,
    @SerializedName(value = "createdBy", alternate = ["CreatedBy"]) val createdBy: String?,
    @SerializedName("createdAt") val createdAt: String?,
    @SerializedName("categories") val categories: List<String>? = null // Añadido campo categorías
)

data class FoodiiMealIngredientDto(
    @SerializedName("ingredientId") val ingredientId: String?,
    @SerializedName("amount") val amount: Double?,
    @SerializedName("ingredientName") val ingredientName: String?,
    @SerializedName("calories") val calories: Double?,
)

data class CreateMealIngredientPayloadDto(
    @SerializedName("ingredientId") val ingredientId: String,
    @SerializedName("amount") val amount: Int,
)

data class CreateMealRequestDto(
    @SerializedName("userId") val userId: String,
    @SerializedName("name") val name: String,
    @SerializedName("date") val date: String,
    @SerializedName("mealTime") val mealTime: String,
    @SerializedName("ingredients") val ingredients: List<CreateMealIngredientPayloadDto>,
    @SerializedName("steps") val steps: List<String>,
    @SerializedName("image") val image: String?,
    @SerializedName("categories") val categories: List<String> = emptyList()
)
