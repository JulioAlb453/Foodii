package com.example.foodii.feature.apifoodii.ingredient.data.datasource.remote.model

import com.google.gson.annotations.SerializedName

data class IngredientResponse(
    @SerializedName("success") val success: Boolean?,
    @SerializedName("data") val ingredients: List<IngredientDto>?
)

data class SingleIngredientResponse(
    @SerializedName("success") val success: Boolean?,
    @SerializedName("data") val ingredient: IngredientDto?
)

data class IngredientDto(
    @SerializedName("id") val id: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("caloriesPer100g") val caloriesPer100g: Double?,
    @SerializedName("createdBy") val createdBy: String?,
    @SerializedName("createdAt") val createdAt: String?
)
